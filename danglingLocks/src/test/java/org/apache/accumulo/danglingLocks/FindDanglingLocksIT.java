/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.accumulo.danglingLocks;

import org.apache.accumulo.core.Constants;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.impl.Tables;
import org.apache.accumulo.core.conf.Property;
import org.apache.accumulo.core.zookeeper.ZooUtil;
import org.apache.accumulo.fate.AdminUtil;
import org.apache.accumulo.fate.ZooStore;
import org.apache.accumulo.fate.zookeeper.IZooReaderWriter;
import org.apache.accumulo.harness.AccumuloClusterHarness;
import org.apache.accumulo.server.zookeeper.ZooReaderWriterFactory;
import org.apache.accumulo.test.util.SlowOps;
import org.apache.zookeeper.KeeperException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class FindDanglingLocksIT extends AccumuloClusterHarness {

    private static final Logger log = LoggerFactory.getLogger(FindDanglingLocksIT.class);
    private static final int NUM_ROWS = 1000;
    private static final long SLOW_SCAN_SLEEP_MS = 250L;

    private Connector connector;

    private static final ExecutorService pool = Executors.newCachedThreadPool();

    private String tableName;

    private String secret;

    private long maxWait;

    private SlowOps slowOps;

    @BeforeClass
    public static void init() throws Exception {

        // AccumuloClusterHarness.setUp();
    }

    @Before
    public void setup() throws Exception {

        log.info("Running setup");
        // AccumuloClusterHarness.setUp();
        // setupCluster();

        connector = getConnector();

        tableName = getUniqueNames(1)[0];

        secret = cluster.getSiteConfiguration().get(Property.INSTANCE_SECRET);

        maxWait = defaultTimeoutSeconds() <= 0 ? 60_000 : ((defaultTimeoutSeconds() * 1000) / 2);

        slowOps = new SlowOps(connector, tableName, maxWait, 1);
    }

    @AfterClass
    public static void cleanup() {
        pool.shutdownNow();
    }

    @Override
    protected int defaultTimeoutSeconds() {
        return 4 * 60;
    }

    @Test
    public void runCompaction() throws Exception {

        setup();

        Instance instance = getConnector().getInstance();
        String tableId = Tables.getTableId(instance, tableName);

        slowOps.startCompactTask();

        AdminUtil.FateStatus withLocks = null;
        List<AdminUtil.TransactionStatus> noLocks = null;

        int maxRetries = 3;

        AdminUtil<String> admin = new AdminUtil<>(false);

        while (maxRetries > 0) {

            try {

                IZooReaderWriter zk = new ZooReaderWriterFactory().getZooReaderWriter(
                        instance.getZooKeepers(), instance.getZooKeepersSessionTimeOut(), secret);

                ZooStore<String> zs = new ZooStore<>(ZooUtil.getRoot(instance) + Constants.ZFATE, zk);

                withLocks = admin.getStatus(zs, zk,
                        ZooUtil.getRoot(instance) + Constants.ZTABLE_LOCKS + "/" + tableId, null, null);

                // call method that does not use locks.
                noLocks = admin.getTransactionStatus(zs, null, null);

                // no zk exception, no need to retry
                break;

            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                fail("Interrupt received - test failed");
                return;
            } catch (KeeperException ex) {
                maxRetries--;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException intr_ex) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        assertNotNull(withLocks);
        assertNotNull(noLocks);

    }
}
