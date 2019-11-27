package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl.mapState;

import org.apache.accumulo.core.data.TableId;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl.ZkMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ZkMapStateTest {

  private final static Logger log = LogManager.getLogger();

  @Test void createNew() throws Exception {

    ZooKeeper mockZooKeeper = EasyMock.createStrictMock(ZooKeeper.class);

    EasyMock.expect(mockZooKeeper.create(EasyMock.isA(String.class), EasyMock.isNull(),
        EasyMock.isA(List.class), EasyMock.isA(CreateMode.class))).andAnswer(new IAnswer<String>() {
      @Override public String answer() throws Throwable {
        return (String)EasyMock.getCurrentArguments()[0];
      }
    });

    EasyMock.expect(mockZooKeeper
        .exists(EasyMock.isA(String.class), EasyMock.anyBoolean()))
        .andAnswer(new IAnswer<Stat>() {
          @Override public Stat answer() throws Throwable {
            Stat stat = new Stat();
            stat.setVersion(123);
            return stat;
          }
        });
//
//    EasyMock.expect(mockZooKeeper
//        .getData(EasyMock.isA(String.class), EasyMock.anyBoolean(), EasyMock.isA(Stat.class)))
//        .andAnswer(new IAnswer<byte[]>() {
//          @Override public byte[] answer() throws Throwable {
//            Stat stat = (Stat) EasyMock.getCurrentArguments()[2];
//            stat.setVersion(123);
//            return new byte[0];
//          }
//        });
    EasyMock.replay(mockZooKeeper);

    ZkMapState state = new ZkMapInit(mockZooKeeper, TableId.of("a1b2"));
    ZkMapState nextState = state.process();

    log.debug("Next: {}", nextState);

    EasyMock.verify(mockZooKeeper);
  }

  @Test public void loadExisting() throws Exception {

    ZooKeeper mockZooKeeper = EasyMock.createStrictMock(ZooKeeper.class);

    EasyMock.expect(mockZooKeeper.create(EasyMock.isA(String.class), EasyMock.isNull(),
        EasyMock.isA(List.class), EasyMock.isA(CreateMode.class)))
        .andThrow(new KeeperException.NodeExistsException("/a/path"));

    EasyMock.expect(mockZooKeeper
        .exists(EasyMock.isA(String.class), EasyMock.anyBoolean()))
        .andAnswer(new IAnswer<Stat>() {
          @Override public Stat answer() throws Throwable {
            Stat stat = new Stat();
            stat.setVersion(123);
            return stat;
          }
        });

    // TODO create map to capture serialized version to return with zoo.getData()
    
    // ZkMap map = new ZkMap(mockZooKeeper, TableId.of("a1b2c3"));

    EasyMock.replay(mockZooKeeper);

    ZkMapState state = new ZkMapInit(mockZooKeeper, TableId.of("a1b2"));
    ZkMapState nextState = state.process();

    log.debug("Next: {}", nextState);

    EasyMock.verify(mockZooKeeper);
  }
}
