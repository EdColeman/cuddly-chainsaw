package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl.mapState;

import org.apache.accumulo.core.data.TableId;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl.Shadow;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl.ZooOps;
import org.apache.zookeeper.ZooKeeper;

public class ZkMapError extends ZkMapState {

  public ZkMapError(ZooKeeper zooKeeper, TableId tableId) {
    super(zooKeeper, tableId);
  }

  public ZkMapState process(){
    return this;
  }

}
