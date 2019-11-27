package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl.mapState;

import org.apache.accumulo.core.data.TableId;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl.Shadow;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl.ZooOps;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.Optional;

public class ZkMapInit extends ZkMapState {

  public ZkMapInit(ZooKeeper zooKeeper, TableId tableId) {
    super(zooKeeper, tableId);
  }

  Shadow.Node node = null;

  public ZkMapState process(){
    if(createNode()){
      Optional<Stat> stat = ZooOps.exists(getZooKeeper(), node.getNodePath());
      if(stat.isPresent()){
        node.setVersion(stat.get());
        return new ZkMapReady(getZooKeeper(),getTableId());
      }
    }else {
      return new ZkMapLoad(getZooKeeper(),getTableId()).process();
    }
    return new ZkMapError(getZooKeeper(), getTableId());
  }

  private boolean createNode(){

   node = Shadow.Factory.create(getTableId().canonical());

    boolean created = ZooOps.create(getZooKeeper(), node.getNodePath());

    return created;
  }
}
