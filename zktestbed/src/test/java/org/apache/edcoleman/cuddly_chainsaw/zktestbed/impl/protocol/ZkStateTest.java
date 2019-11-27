package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl.protocol;

import org.apache.accumulo.core.data.TableId;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.ZkClient;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.ZkServer;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl.ZkMap;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl.ZooOps;
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

import java.util.StringJoiner;

class ZkStateTest {

  private final static Logger log = LogManager.getLogger();

  private static final String zkPathBase = "/accumulo/123/config";

  private static ZkServer zk;

  @BeforeAll public static void init() {
    zk = new ZkServer();
  }

  /**
   * IT - uses test zookeeper
   *
   * @throws Exception an exception is a test failure
   */
  @Test public void simple() throws Exception {

    ZkClient client = new ZkClient();

    ZooKeeper zooKeeper = client.connect("localhost:" + zk.getZkPort());

    ZooOps.create(zooKeeper, "/accumulo");
    ZooOps.create(zooKeeper, "/accumulo/123");
    ZooOps.create(zooKeeper, "/accumulo/123/config");
    ZooOps.create(zooKeeper, "/accumulo/123/config/aTable");

    Stat s = zooKeeper.exists("/accumulo/123/config/aTable", false);

    Stat z = new Stat(1L, 2L, 3L, 4L, 5, 6, 7, 8L, 9, 10, 11L);

    log.info("A Stat {}", s);
    log.info("A Stat {}", z);
    log.info("A Stat {}", ZooOps.prettyStat(z));

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

  @Override public String toString() {
    return new StringJoiner(", ", ZkStateTest.class.getSimpleName() + "[", "]").toString();
  }
}
