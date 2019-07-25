package org.apache.edcoleman.cuddly_chainsaw.du;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.apache.accumulo.minicluster.MiniAccumuloCluster;
import org.apache.hadoop.io.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DUCommandTest {

  private static Logger log = LogManager.getLogger();

  private static MiniAccumuloCluster accumulo = null;
  private static Connector conn = null;

  private final static String testPasswd = "testPwd123";

  @BeforeAll public static void init() {

    try {

      Path miniTempDir = FileSystems.getDefault().getPath("/tmp", "mini_tests");

      Files.walkFileTree(miniTempDir, new SimpleFileVisitor<Path>() {
        @Override public FileVisitResult postVisitDirectory(Path dir, IOException exc)
            throws IOException {
          Files.delete(dir);
          return FileVisitResult.CONTINUE;
        }

        @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
            throws IOException {
          Files.delete(file);
          return FileVisitResult.CONTINUE;
        }
      });

      if (Files.exists(miniTempDir)) {
        Files.deleteIfExists(miniTempDir);
        Files.createDirectories(miniTempDir);
      }

      accumulo = new MiniAccumuloCluster(miniTempDir.toFile(), testPasswd);

      accumulo.start();

      conn = accumulo.getConnector("root", testPasswd);

      log.info("Mini is {} - dir {} - {}", accumulo,
          miniTempDir.toString());
    } catch (AccumuloException | AccumuloSecurityException | IOException ex) {
      throw new IllegalStateException("Failed to start mini cluster", ex);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }

  @AfterAll public static void shutdown() {

    if (accumulo != null) {
      try {
        accumulo.stop();
      } catch (IOException ex) {
        log.debug("IOException on mini cluster shutdown", ex);
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
      accumulo = null;
    }
  }

  @Test void humanReadableByteCount() {

    assertAll("test suffix", () -> assertTrue(DUCommand.humanReadableByteCount(100).endsWith("B")),
        () -> assertTrue(DUCommand.humanReadableByteCount(1023).endsWith("B")),
        () -> assertTrue(DUCommand.humanReadableByteCount(1024).endsWith("KB")),
        () -> assertTrue(DUCommand.humanReadableByteCount((1024 * 1024) - 1024).endsWith("KB")),
        () -> assertTrue(DUCommand.humanReadableByteCount(1024 * 1024).endsWith("MB")),
        () -> assertTrue(DUCommand.humanReadableByteCount(1024 * 1024 * 1024 - 1).endsWith("MB")),
        () -> assertTrue(DUCommand.humanReadableByteCount(1024 * 1024 * 1024).endsWith("GB")),
        () -> assertTrue(
            DUCommand.humanReadableByteCount(1024L * 1024 * 1024 * 1024 - 1).endsWith("GB")),
        () -> assertTrue(
            DUCommand.humanReadableByteCount(1024L * 1024 * 1024 * 1024).endsWith("TB")));
  }

  @Test void mini() throws Exception {

    log.debug("Initial tables {}", conn.tableOperations().tableIdMap());

    if (!conn.tableOperations().exists("table_1")) {
      conn.tableOperations().create("table_1");
    } else {
      //TODO delete or otherwise not create additional data?
    }

    conn.securityOperations().changeUserAuthorizations("root", new Authorizations("public"));

    log.debug("Current tables {}", conn.tableOperations().tableIdMap());

    BatchWriterConfig config = new BatchWriterConfig();
    config.setMaxMemory(10000000L); // bytes available to batch writer for buffering mutations

    int dataCount = 1;

    try (BatchWriter writer = conn.createBatchWriter("table_1", config)) {

      ColumnVisibility colVis = new ColumnVisibility("public");

      for (int r = 0; r < 10; r++) {

        Text rowID = new Text("r:" + r);

        for (int cf = 1; cf < 4; cf++) {

          Text colFam = new Text("colFam:" + cf);

          Text colQual = new Text("cq:" + (1021 * (r + 1) ^ (cf * 17)));

          long timestamp = System.currentTimeMillis();

          Value value = new Value(BigInteger.valueOf(dataCount++).toByteArray());

          Mutation mutation = new Mutation(rowID);
          mutation.put(colFam, colQual, colVis, timestamp, value);

          log.trace("w: {}", () -> new Text(mutation.getRow()));

          writer.addMutation(mutation);
        }
      }
      writer.flush();
    }

    // conn.tableOperations().flush("table_1", null, null, true);
    conn.tableOperations().compact("table_1", null, null, true, true);

    // echo...

    Authorizations auths = new Authorizations("public");

    Scanner scan =
        conn.createScanner("table_1", auths);

    scan.setRange(new Range());

    // scan.fetchColumnFamily(new Text("attributes"));

    for(Map.Entry<Key,Value> entry : scan) {
      Text row = entry.getKey().getRow();
      Value value = entry.getValue();

      log.debug("Read: {}", row);
    }
  }

  static class BadClass{
    public BadClass(){
      throw new UnsupportedOperationException("Opps");
    }
  }
}
