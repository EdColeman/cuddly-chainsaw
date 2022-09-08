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

import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.fate.Repo;
import org.apache.accumulo.fate.zookeeper.IZooReaderWriter;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

class FindDanglingLocksTest {

    private static final Logger log = LoggerFactory.getLogger(FindDanglingLocksTest.class);

    private static FindDanglingLocks.FindLockOpts findLockOpts;

    @Before
    public static void init() {
        findLockOpts = new FindDanglingLocks.FindLockOpts();
        findLockOpts.parseArgs(FindDanglingLocks.class.getName(), new String[]{"--instance", "uno", "-u", "root", "-p", "secret"});

        log.info("Opts:{}", findLockOpts.instance);

    }

    @Test
    public void noLocks() throws Exception {

        String instanceId = UUID.randomUUID().toString();

        Instance instance = createMock(Instance.class);
        expect(instance.getInstanceID()).andReturn(instanceId).anyTimes();

        IZooReaderWriter zrw = createMock(IZooReaderWriter.class);
        expect(zrw.putPersistentData(eq("/accumulo/" + instanceId + "/fate"), anyObject(), anyObject())).andReturn(false);
        expect(zrw.getChildren(eq("/accumulo/" + instanceId + "/fate"))).andReturn(Collections.emptyList());
        expect(zrw.getChildren(eq("/accumulo/" + instanceId + "/table_locks"))).andReturn(Collections.emptyList()).anyTimes();
        replay(instance, zrw);

        FindDanglingLocks finder = new FindDanglingLocks(findLockOpts, instance, zrw);
        finder.execute();

        verify(instance, zrw);
    }
    @Test
    public void fateWithLocks() throws Exception {

        String instanceId = UUID.randomUUID().toString();

        Instance instance = createMock(Instance.class);
        expect(instance.getInstanceID()).andReturn(instanceId).anyTimes();

        Repo<String> repo = EasyMock.createMock(Repo.class);

        IZooReaderWriter zrw = createMock(IZooReaderWriter.class);
        expect(zrw.putPersistentData(eq("/accumulo/" + instanceId + "/fate"), anyObject(), anyObject())).andReturn(false);
        String txid = "tx_55dc0e71c60e7979";
        expect(zrw.getChildren(eq("/accumulo/" + instanceId + "/fate"))).andReturn(Arrays.asList(txid));
        expect(zrw.getChildren(eq("/accumulo/" + instanceId + "/fate/" + txid))).andReturn(Arrays.asList(txid, "repo_000000")).once();
        expect(zrw.getData(eq("/accumulo/" + instanceId + "/fate/"+txid+"/prop_debug"), anyObject())).andReturn(new String("Smock_test").getBytes(UTF_8));
       // expect(zrw.getData(eq("/accumulo/" + instanceId + "/fate/"+txid+"/repo_000000"), anyObject())).andReturn(repo.).getBytes(StandardCharsets.UTF_8));
        expect(zrw.getChildren(eq("/accumulo/" + instanceId + "/table_locks"))).andReturn(Collections.emptyList()).anyTimes();
        replay(instance, zrw);

        FindDanglingLocks finder = new FindDanglingLocks(findLockOpts, instance, zrw);
        finder.execute();

        verify(instance, zrw);
    }
    @Test
    public void mockDanglingLocks() throws Exception {

        String instanceId = UUID.randomUUID().toString();

        Instance instance = createMock(Instance.class);
        expect(instance.getInstanceID()).andReturn(instanceId).anyTimes();

        Repo<String> repo = EasyMock.createMock(Repo.class);

        IZooReaderWriter zrw = createMock(IZooReaderWriter.class);
        expect(zrw.putPersistentData(eq("/accumulo/" + instanceId + "/fate"), anyObject(), anyObject())).andReturn(false);
        String txid = "55dc0e71c60e7979";
        expect(zrw.getChildren(eq("/accumulo/" + instanceId + "/fate"))).andReturn(Arrays.asList());
        // expect(zrw.getChildren(eq("/accumulo/" + instanceId + "/fate/" + txid))).andReturn(Arrays.asList(txid, "repo_000000")).once();
        // expect(zrw.getData(eq("/accumulo/" + instanceId + "/fate/"+txid+"/prop_debug"), anyObject())).andReturn(new String("Smock_test").getBytes(StandardCharsets.UTF_8));
        expect(zrw.getChildren(eq("/accumulo/" + instanceId + "/table_locks"))).andReturn(Arrays.asList("+default", "123", "124")).anyTimes();
        expect(zrw.getChildren(eq("/accumulo/" + instanceId + "/table_locks/+default"))).andReturn(Arrays.asList("lock-0000000000", "lock-0000000002")).anyTimes();
        expect(zrw.getData(eq("/accumulo/" + instanceId + "/table_locks/+default/lock-0000000000"),anyObject()))
                .andReturn(new String("READ:55dc0e71c60e7979").getBytes(UTF_8)).anyTimes();
        expect(zrw.getData(eq("/accumulo/" + instanceId + "/table_locks/+default/lock-0000000002"),anyObject()))
                .andReturn(new String("READ:55dc0e71c60e7979").getBytes(UTF_8)).anyTimes();

        expect(zrw.getChildren(eq("/accumulo/" + instanceId + "/table_locks/123"))).andReturn(Arrays.asList("lock-0000000000", "lock-0000000002")).anyTimes();
        expect(zrw.getData(eq("/accumulo/" + instanceId + "/table_locks/123/lock-0000000000"),anyObject()))
                .andReturn(new String("READ:55dc0e71c60e7979").getBytes(UTF_8)).anyTimes();
        expect(zrw.getData(eq("/accumulo/" + instanceId + "/table_locks/123/lock-0000000002"),anyObject()))
                .andReturn(new String("WRITE:55dc0e71c60e7979").getBytes(UTF_8)).anyTimes();

        expect(zrw.getChildren(eq("/accumulo/" + instanceId + "/table_locks/124"))).andReturn(Arrays.asList("lock-0000000000", "lock-0000000002")).anyTimes();
        expect(zrw.getData(eq("/accumulo/" + instanceId + "/table_locks/124/lock-0000000000"),anyObject()))
                .andReturn(new String("+WRITE:55dc0e71c60e7979").getBytes(UTF_8)).anyTimes();
        expect(zrw.getData(eq("/accumulo/" + instanceId + "/table_locks/124/lock-0000000002"),anyObject()))
                .andReturn(new String("+READ:55dc0e71c60e7979").getBytes(UTF_8)).anyTimes();

        replay(instance, zrw);

        FindDanglingLocks finder = new FindDanglingLocks(findLockOpts, instance, zrw);
        finder.execute();

        verify(instance, zrw);
    }
    @Test
    public void hello() throws Exception {
        FindDanglingLocks finder = new FindDanglingLocks(findLockOpts);
        finder.execute();
    }

    @Test
    public void rtest() {
        String v = "[R:+default, R:2, W:3]";
        // FindDanglingLocks.parseLocks(v).forEach(p -> log.info("Parts: {}", FindDanglingLocks.parseLockInfo(p)));
    }

}