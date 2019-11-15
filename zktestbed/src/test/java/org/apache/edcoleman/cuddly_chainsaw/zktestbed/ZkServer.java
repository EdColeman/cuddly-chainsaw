package org.apache.edcoleman.cuddly_chainsaw.zktestbed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.client.FourLetterWordMain;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;

import java.io.File;

public class ZkServer {

  private final static Logger log = LogManager.getLogger();

  // private final ZooKeeperServerMain server;
  private final int zkPort = 51011;

  // zookeeper config
  private final int tickTime = 2000;
  private final int numConnections = 5000;

  public ZkServer() {

    try {

      String dataDirectory = System.getProperty("java.io.tmpdir");

      File file = new File(getClass().getClassLoader().getResource("zoo.cfg").getFile());

      log.info("Found?: {}", file.exists());

      //  ZkServer.class.getClassLoader().getResourceAsStream("zoo.cfg");

      File dir = new File(dataDirectory, "zookeeper").getAbsoluteFile();

      new Thread() {
        public void run() {
          try {
            log.info("Starting zookeeper server");
            ServerConfig config = new ServerConfig();
            config.parse(file.getAbsolutePath());
            new ZooKeeperServerMain().runFromConfig(config);
          } catch (Exception ex) {
            throw new IllegalStateException(ex);
          }
        }
      }.start();

      //server = new ZooKeeperServer(dir, dir, tickTime);

//      ServerCnxnFactory standaloneServerFactory = ServerCnxnFactory
//          .createFactory(0, numConnections);

      //zkPort = standaloneServerFactory.getLocalPort();

      //log.info("EPH Port:{}", zkPort);
      //standaloneServerFactory.startup(server);

      //log.info("S:{}", server.getServerCnxnFactory().getLocalAddress());

    } catch (Exception ex) {
      log.error("Failed to start zookeeper", ex);
      throw new IllegalStateException(ex);
    }
  }

  public int getZkPort() {
    return zkPort;
  }
  //
  // //  public ServerStats x(){
  //    return server.serverStats();
  //  }
  //  private void close() {
  //    server.shutdown();
  //  }

  public static void main(String[] args) throws Exception {

    System.setProperty("4lw.commands.whitelist", "wchp");

    ZkServer zk = new ZkServer();

    ZkClient client = new ZkClient();

    try {

      ZooKeeper zoo = client.connect("localhost:" + zk.getZkPort());

      log.info("Client is {}", client);

      FourLetterWordMain flw = new FourLetterWordMain();

      log.info("FLW: HC: {}", flw.send4LetterWord("localhost", zk.getZkPort(), "ruok"));
      // log.info("FLW: WCHS: {}",  flw.send4LetterWord("localhost", zk.getZkPort(), "wchs"));
      log.info("FLW: WCHS: {}", flw.send4LetterWord("localhost", zk.getZkPort(), "wchs"));
      log.info("FLW: WCHC: {}", flw.send4LetterWord("localhost", zk.getZkPort(), "wchc"));
      log.info("FLW: WCHP: {}", flw.send4LetterWord("localhost", zk.getZkPort(), "wchp"));

    } finally {
      client.close();
    }

    Thread.currentThread().join(20_000);
    // zk.close();
  }
}

