package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.accumulo.core.data.TableId;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.PropValue;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.ZkClient;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.ZkServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class StateProtocolTest {

  private final static Logger log = LogManager.getLogger();

  private static final String zkPathBase = "/accumulo/123/config";

  private static ZkServer zk;

  @BeforeAll public static void init() {
    zk = new ZkServer();
  }

  @Test public void simple() throws Exception {

    ZkClient client = new ZkClient();

    ZooKeeper zooKeeper = client.connect("localhost:" + zk.getZkPort());

    create(zooKeeper, "/accumulo");
    create(zooKeeper, "/accumulo/123");
    create(zooKeeper, "/accumulo/123/config");
    create(zooKeeper, "/accumulo/123/config/aTable");

    ZkMap zkMap = new ZkMap(zooKeeper, TableId.of("aTable"));
    zkMap.update("foo", "value");

  }

  @Test public void zkMock() throws Exception {
    ZooKeeper mockZooKeeper = EasyMock.createNiceMock(ZooKeeper.class);

    EasyMock.expect(mockZooKeeper
        .getData(EasyMock.isA(String.class), EasyMock.anyBoolean(), EasyMock.isA(Stat.class)))
        .andAnswer(new IAnswer<byte[]>() {
          @Override public byte[] answer() throws Throwable {
            Stat stat = (Stat) EasyMock.getCurrentArguments()[2];
            stat.setVersion(123);
            return new byte[0];
          }
        });
    EasyMock.replay(mockZooKeeper);

    ZkMap zkMap = new ZkMap(mockZooKeeper, TableId.of("aTable"));
    zkMap.update("foo", "value");

    Stat stat = new Stat();
    byte[] data = mockZooKeeper.getData(zkPathBase + "/3322", false, stat);

    log.debug("data {}", data);
    log.debug("stat: {}", stat);
  }

  private static class ZkMap {

    private final static Gson gson = new Gson();
    private static final TypeToken<Map<String,PropValue>> gsonDataType = new TypeToken<>() {
    };

    private final ZooKeeper zooKeeper;
    private final TableId tableId;

    private Map<String,String> data = new HashMap<>();
    private int version = -1;

    public ZkMap(final ZooKeeper zooKeeper, final TableId tableId) {
      this.zooKeeper = zooKeeper;
      this.tableId = tableId;
    }

    public boolean update(String name, String value) {

      ZkUpdateProtocol updater = new ZkUpdateProtocol(zooKeeper, this);
      updater.update(name, value);

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
      this.version = version;
    }

    enum MapState {
      Init {
        @Override MapState nextState() {
          return Created;
        }
      }, Created {
        @Override MapState nextState() {
          return Unloaded;
        }
      }, Unloaded {
        @Override MapState nextState() {
          return Loaded;
        }
      }, Loaded {
        @Override MapState nextState() {
          return Deleting;
        }
      }, Deleting {
        @Override MapState nextState() {
          return this;
        }
      };

      abstract MapState nextState();
    }

  }

  private static class ZkStateCtx {

    private final ZooKeeper zooKeeper;
    private final ZkMap backingMap;
    private Optional<KeeperException> keeperException;

    private final AtomicInteger zkConnectionErrors = new AtomicInteger();
    private final AtomicInteger zkNodeErrors = new AtomicInteger();

    public ZkStateCtx(final ZooKeeper zooKeeper, final ZkMap backingMap) {
      this.zooKeeper = zooKeeper;
      this.backingMap = backingMap;
    }

    public void setZkException(KeeperException ex) {
      keeperException = Optional.of(ex);
    }
  }

  private static class ZkUpdateProtocol {

    private final ZkStateCtx ctx;
    private ZkStateProtocol next;

    public ZkUpdateProtocol(final ZooKeeper zooKeeper, final ZkMap zkMap) {
      this.ctx = new ZkStateCtx(zooKeeper, zkMap);
    }

    public void update(final String name, final String value) {
      next = new CheckVersion();
      next.handle(ctx);
    }

    public boolean handle(final ZkStateCtx ctx) {
      return false;
    }

  }

  private interface ZkStateProtocol {
    boolean handle(final ZkStateCtx ctx);
  }

  private static class CheckVersion implements ZkStateProtocol {

    @Override public boolean handle(ZkStateCtx ctx) {
      try {
        Stat stat = new Stat();
        byte[] data = ctx.zooKeeper.getData(zkPathBase + "/" + ctx.backingMap.tableId, false, stat);

        boolean same = checkVersion(stat, ctx);
        if (!same) {
          ctx.backingMap.update(data);
          ctx.backingMap.setZNodeVersion(stat.getVersion());
        }

        log.info("Stat: {}", stat);
        return false;
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      } catch (KeeperException ex) {
        ctx.setZkException(ex);
      }
      return false;
    }

    boolean checkVersion(final Stat stat, final ZkStateCtx ctx) {
      return stat.getVersion() == ctx.backingMap.version;
    }
  }

  private static class ZkExceptionHandler implements ZkStateProtocol {

    @Override public boolean handle(ZkStateCtx ctx) {
      return false;
    }
  }

  private void create(final ZooKeeper zoo, String path) {
    try {
      zoo.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    } catch (KeeperException.NodeExistsException ex) {
      return;
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    } catch (KeeperException ex) {
      throw new IllegalStateException(ex);
    }
  }
}
