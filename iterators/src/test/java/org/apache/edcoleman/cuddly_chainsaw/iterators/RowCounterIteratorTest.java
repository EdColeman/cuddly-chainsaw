package org.apache.edcoleman.cuddly_chainsaw.iterators;


import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.apache.accumulo.core.security.TablePermission;
import org.apache.accumulo.minicluster.MiniAccumuloCluster;
import org.apache.hadoop.io.Text;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RowCounterIteratorTest {

    @TempDir
    static Path tempDirectory;

    private static MiniAccumuloCluster accumulo;
    private static Connector conn;

    @BeforeAll
    public static void startMini() throws Exception {
        accumulo = new MiniAccumuloCluster(tempDirectory.toFile(), "password");
        accumulo.start();

        Instance instance = new ZooKeeperInstance(accumulo.getInstanceName(), accumulo.getZooKeepers());
        conn = instance.getConnector("root", new PasswordToken("password"));
    }

    @AfterAll
    public static void stopMini() throws IOException, InterruptedException {
        accumulo.stop();
    }

    @Test
    public void x() throws Exception {

        conn.tableOperations().create("table1");

        BatchWriterConfig batchWriterConfig = new BatchWriterConfig();
        batchWriterConfig.setMaxMemory(8*1024);

        BatchWriter batchWriter = conn.createBatchWriter("table1", batchWriterConfig);

        // conn.securityOperations().grantTablePermission("root", "table1", TablePermission.READ);
        conn.securityOperations().changeUserAuthorizations("root", new Authorizations("public"));

        // create data
        int numRows = 20;
        int numValsPerRow = 6;

        // Text colFam = new Text("ColFam1");
        ColumnVisibility colVis = new ColumnVisibility("public");

        for(int i = 0; i < numRows; i++){
            for(int j = 0; j < numValsPerRow; j++){
                Text rowId = new Text(String.format("r:%03d", i));

                Text colQual = new Text(String.format("%08x", (7919 * ((619 * i) ^ (1471 * j)))));

                // Text colQual = new Text(String.format("%03d", j));
                Text colFam = new Text(String.format("ColFam-%01d", (j % 2)+1));

                long timestamp = System.currentTimeMillis();

                Value v = new Value(String.format("%03d:%016X", i, timestamp).getBytes());

                Mutation m = new Mutation(rowId);
                m.put(colFam,colQual, colVis, timestamp, v);

                batchWriter.addMutation(m);
            }
        }

        batchWriter.close();

        Scanner scanner = conn.createScanner("table1", new Authorizations("public"));

        scanner.addScanIterator(new IteratorSetting(25, "rowCount", "org.apache.edcoleman.cuddly_chainsaw.iterators.RowCounterIterator"));
        scanner.setRange(new Range());

        for(Map.Entry<Key,Value> entry : scanner){
            Text row = entry.getKey().getRow();
            System.out.println("R: " + row.toString() + " : " + entry.getKey().toString());
        }

        scanner.close();


        System.out.println("Test");

        assertEquals("X", "Y");
    }
}
