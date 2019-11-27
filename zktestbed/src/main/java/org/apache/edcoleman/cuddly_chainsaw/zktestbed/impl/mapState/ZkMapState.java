package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl.mapState;

import org.apache.accumulo.core.data.TableId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.ZooKeeper;

import java.util.StringJoiner;

public abstract class ZkMapState {

  private final static Logger log = LogManager.getLogger();

  private final ZooKeeper zooKeeper;
  private final TableId tableId;

  public ZkMapState(final ZooKeeper zooKeeper, final TableId tableId) {
    this.zooKeeper = zooKeeper;
    this.tableId = tableId;
  }

  public ZooKeeper getZooKeeper() {
    return zooKeeper;
  }

  public TableId getTableId(){
    return tableId;
  }

  public abstract ZkMapState process();

  @Override public String toString() {
    return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
        .add("tableId=" + tableId).toString();
  }
}
