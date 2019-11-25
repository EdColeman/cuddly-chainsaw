package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.accumulo.core.data.TableId;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.PropValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class ZkMap {

  private final static Logger log = LogManager.getLogger();

  private final static Gson gson = new Gson();
  private static final TypeToken<Map<String,PropValue>> gsonDataType = new TypeToken<>() {
  };

  private final ZooKeeper zooKeeper;
  private final TableId tableId;
  private AtomicLong zkNodeVersion = new AtomicLong(-1);

  private Map<String,String> data = new HashMap<>();

  private MapState state = MapState.Invalid;

  public ZkMap(final ZooKeeper zooKeeper, final TableId tableId) throws IllegalStateException {
    this.zooKeeper = zooKeeper;
    this.tableId = tableId;
    state = MapState.Init;
    state = state.nextState(zooKeeper, tableId);
  }

  public static String getConfigPath(final TableId id) {
    return "/accumulo/12345/" + id.canonical();
  }

  public MapState getState() {
    return state;
  }

  public TableId getTableId() {
    return tableId;
  }

  public long getVersion() {
    return zkNodeVersion.get();
  }

  public boolean update(String name, String value) {

    //    StateProtocolTest.ZkUpdateProtocol updater = new StateProtocolTest.ZkUpdateProtocol(zooKeeper, this);
    //    updater.update(name, value);

    return false;
  }

  private byte[] toBytes() {
    String v = gson.toJson(data);
    try {
      ByteArrayOutputStream bos;

      bos = new ByteArrayOutputStream(v.length() + 32);
      DataOutputStream out = new DataOutputStream(bos);

      out.writeInt(v.length());
      out.writeUTF(v);

      out.close();
      return bos.toByteArray();
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  private void fromBytes(final byte[] bytes) {

    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
    DataInputStream in = new DataInputStream(bis);

    try {
      String json = in.readUTF();
      data.clear();
      data.putAll(gson.fromJson(json, gsonDataType.getType()));

    } catch (IOException ex) {
      throw new IllegalStateException("Failed to convert from byte[]", ex);
    }

  }

  public void update(byte[] data) {

  }

  public void setZNodeVersion(final int version) {
    this.zkNodeVersion.set(version);
  }

  enum MapState {
    Invalid {
      @Override MapState nextState(final ZooKeeper zooKeeper, final TableId tableId) {
        return this;
      }
    }, Init {
      @Override MapState nextState(final ZooKeeper zooKeeper, final TableId tableId) {
        String path = ZkMap.getConfigPath(tableId);
        try {
          String n = zooKeeper
              .create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
          if(n != null){
            return MapState.Created.nextState(zooKeeper, tableId);
          }
          return MapState.Invalid;
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          return Invalid;
        } catch (KeeperException.NoNodeException e) {
          // thrown if parent does not exist.
          throw new IllegalStateException("Parent node(s) do not exist for " + path, e);
        } catch (KeeperException.NodeExistsException e) {
          // node exists - try to load data
          return MapState.Loaded.nextState(zooKeeper, tableId);
        } catch (KeeperException e) {
          throw new IllegalStateException("Could not create zkMap, unhandled zookeeper exception",
              e);
        }
      }
    }, Created {
      @Override MapState nextState(final ZooKeeper zooKeeper, final TableId tableId) {
        return Unloaded;
      }
    }, Unloaded {
      @Override MapState nextState(final ZooKeeper zooKeeper, final TableId tableId) {
        return Loaded;
      }
    }, Loaded {
      @Override MapState nextState(final ZooKeeper zooKeeper, final TableId tableId) {
        return Deleting;
      }
    }, Deleting {
      @Override MapState nextState(final ZooKeeper zooKeeper, final TableId tableId) {
        return this;
      }
    };

    abstract MapState nextState(final ZooKeeper zooKeeper, final TableId tableId);
  }

}
