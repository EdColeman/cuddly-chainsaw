package org.apache.edcoleman.cuddly_chainsaw.zktestbed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.client.FourLetterWordMain;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ChildWatcherTests {

  private final static Logger log = LogManager.getLogger();

  private static ZkServer server;

  @BeforeAll public static void setup() {
    server = new ZkServer();
  }

  @Test public void connect() throws Exception {

    ZkClient client = new ZkClient();

    ZooKeeper zoo = client.connect("localhost:" + server.getZkPort());

    log.info("Connection: {}", zoo);

    Watcher w = new LoggingWatcher();

    zoo.register(w);

    log.info("create /a");
    create(zoo, "/a");


    log.info("watch /a");
    // zoo.exists("/a", true);

    log.info("create /a/b");
    create(zoo, "/a/b");
    // zoo.exists("/a/b", true);
    zoo.getChildren("/a", true);
    zoo.getChildren("/a/b", true);

    FourLetterWordMain flw = new FourLetterWordMain();

    log.info("FLW: HC: {}", flw.send4LetterWord("localhost", server.getZkPort(), "ruok"));
    // log.info("FLW: WCHS: {}",  flw.send4LetterWord("localhost", server.getZkPort(), "wchs"));
    log.info("FLW: WCHS: {}", flw.send4LetterWord("localhost", server.getZkPort(), "wchs"));
    log.info("FLW: WCHC: {}", flw.send4LetterWord("localhost", server.getZkPort(), "wchc"));
    log.info("FLW: WCHP: {}", flw.send4LetterWord("localhost", server.getZkPort(), "wchp"));

    log.info("watch /a/b");
    zoo.getChildren("/a/b", true);

    // zoo.getChildren("/a/b", false);

    for(int i = 0; i < 10; i++) {
      Thread.sleep(500);
    }

    log.info("WATCHERS SET");

    log.info("FLW: HC: {}", flw.send4LetterWord("localhost", server.getZkPort(), "ruok"));
    // log.info("FLW: WCHS: {}",  flw.send4LetterWord("localhost", server.getZkPort(), "wchs"));
    log.info("FLW: WCHS: {}", flw.send4LetterWord("localhost", server.getZkPort(), "wchs"));
    log.info("FLW: WCHC: {}", flw.send4LetterWord("localhost", server.getZkPort(), "wchc"));
    log.info("FLW: WCHP: {}", flw.send4LetterWord("localhost", server.getZkPort(), "wchp"));

    log.info("Create child /a/b/c");

    create(zoo, "/a/b/c");

    log.info("FLW: HC: {}", flw.send4LetterWord("localhost", server.getZkPort(), "ruok"));
    // log.info("FLW: WCHS: {}",  flw.send4LetterWord("localhost", server.getZkPort(), "wchs"));
    log.info("FLW: WCHS: {}", flw.send4LetterWord("localhost", server.getZkPort(), "wchs"));
    log.info("FLW: WCHC: {}", flw.send4LetterWord("localhost", server.getZkPort(), "wchc"));
    log.info("FLW: WCHP: {}", flw.send4LetterWord("localhost", server.getZkPort(), "wchp"));

  }

  private void create(final ZooKeeper zoo, String path){
    try {
      zoo.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }catch(KeeperException.NodeExistsException ex){
      return;
    }catch (InterruptedException ex){
      Thread.currentThread().interrupt();
    } catch (KeeperException ex) {
      throw new IllegalStateException(ex);
    }
  }
  private static class LoggingWatcher implements Watcher {

    @Override public void process(WatchedEvent event) {
      log.info("ZK Event: {}", event);
    }
  }
}
