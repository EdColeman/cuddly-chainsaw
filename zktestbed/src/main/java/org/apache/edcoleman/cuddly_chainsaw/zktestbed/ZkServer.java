package org.apache.edcoleman.cuddly_chainsaw.zktestbed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.client.FourLetterWordMain;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ServerStats;
import org.apache.zookeeper.server.ZooKeeperServer;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZkServer {

  private final static Logger log = LogManager.getLogger();

  private final ZooKeeperServer server;
  private final int zkPort;

  // zookeeper config
  private final int tickTime = 2000;
  private final int numConnections = 5000;

  public ZkServer() {

    try {

      String dataDirectory = System.getProperty("java.io.tmpdir");

      File dir = new File(dataDirectory, "zookeeper").getAbsoluteFile();

      server = new ZooKeeperServer(dir, dir, tickTime);

      ServerCnxnFactory standaloneServerFactory = ServerCnxnFactory
          .createFactory(0, numConnections);

      zkPort = standaloneServerFactory.getLocalPort();

      log.info("EPH Port:{}", zkPort);
      standaloneServerFactory.startup(server);

      log.info("S:{}", server.getServerCnxnFactory().getLocalAddress());

    } catch (Exception ex) {
      log.error("Failed to start zookeeper", ex);
      throw new IllegalStateException(ex);
    }
  }

  public int getZkPort() {
    return zkPort;
  }

  public ServerStats x(){
    return server.serverStats();
  }
  private void close() {
    server.shutdown();
  }

  private static class ZkClient {

    private ZooKeeper zoo;
    CountDownLatch connectionLatch = new CountDownLatch(1);

    public ZooKeeper connect(String host) throws IOException, InterruptedException {
      zoo = new ZooKeeper(host, 2000, new Watcher() {
        public void process(WatchedEvent we) {
          if (we.getState() == Event.KeeperState.SyncConnected) {
            connectionLatch.countDown();
          }
        }
      });

      connectionLatch.await();
      return zoo;
    }

    public void close() throws InterruptedException {
      zoo.close();
    }
  }

  public static void main(String[] args) throws Exception {

    System.setProperty("4lw.commands.whitelist", "wchp");

    ZkServer zk = new ZkServer();

    ZkClient client = new ZkClient();

    try {

      ZooKeeper zoo = client.connect("localhost:" + zk.getZkPort());

      log.info("Client is {}", client);
      log.info("Stats: {}", zk.server.serverStats());

      FourLetterWordMain flw = new FourLetterWordMain();

      log.info("FLW: HC: {}",  flw.send4LetterWord("localhost", zk.getZkPort(), "ruok"));
      // log.info("FLW: WCHS: {}",  flw.send4LetterWord("localhost", zk.getZkPort(), "wchs"));
      log.info("FLW: WCHS: {}",  flw.send4LetterWord("localhost", zk.getZkPort(), "wchs"));
      log.info("FLW: WCHC: {}",  flw.send4LetterWord("localhost", zk.getZkPort(), "wchc"));
      log.info("FLW: WCHP: {}",  flw.send4LetterWord("localhost", zk.getZkPort(), "wchp"));

    } finally {
      client.close();
    }

    Thread.currentThread().join(20_000);
    zk.close();
  }
}

