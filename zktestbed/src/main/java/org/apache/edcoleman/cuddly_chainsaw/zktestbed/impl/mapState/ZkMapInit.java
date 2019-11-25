package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl.mapState;

import org.apache.accumulo.core.data.TableId;
import org.apache.zookeeper.ZooKeeper;

public class ZkMapInit extends ZkMapState {

  public ZkMapInit(ZooKeeper zooKeeper, TableId tableId) {
    super(zooKeeper, tableId);
  }

  public ZkMapState process(){
    return this;
  }
}
