package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl;

import org.apache.accumulo.core.data.TableId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ZkMapTest {

  private final static Logger log = LogManager.getLogger();

  /**
   * Create an instance of a new ZkMap that does not exist in the backing zookeeper store.
   */
  @Test void createNew() throws KeeperException, InterruptedException {

    ZooKeeper mockZooKeeper = EasyMock.createStrictMock(ZooKeeper.class);

    EasyMock.expect(mockZooKeeper.create(EasyMock.isA(String.class), EasyMock.isNull(),
        EasyMock.isA(List.class), EasyMock.isA(CreateMode.class))).andAnswer(new IAnswer<String>() {
      @Override public String answer() throws Throwable {
        return (String)EasyMock.getCurrentArguments()[0];
      }
    });
/*
    EasyMock.expect(mockZooKeeper
        .getData(EasyMock.isA(String.class), EasyMock.anyBoolean(), EasyMock.isA(Stat.class)))
        .andAnswer(new IAnswer<byte[]>() {
          @Override public byte[] answer() throws Throwable {
            Stat stat = (Stat) EasyMock.getCurrentArguments()[2];
            stat.setVersion(123);
            return new byte[0];
          }
        });
*/
    EasyMock.replay(mockZooKeeper);

    ZkMap zkMap = new ZkMap(mockZooKeeper, TableId.of("aTable"));

    log.info("Map State: {}", zkMap.getState());

    EasyMock.verify(mockZooKeeper);
  }

  /**
   * Create an instance of a new ZkMap that exists on the backing store - the backing store
   * should populate this instance with the existing values.
   */
  @Test void createExisting() {
  }
}
