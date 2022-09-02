package org.apache.accumulo.danglingLocks;

import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.fate.zookeeper.IZooReaderWriter;
import org.apache.zookeeper.KeeperException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

class FindLocksTest {

    private static final Logger log = LoggerFactory.getLogger(FindLocksTest.class);

    private static FindLocks.FindLockOpts findLockOpts;

    @BeforeAll
    public static void init() {
        findLockOpts = new FindLocks.FindLockOpts();
        findLockOpts.parseArgs(FindLocks.class.getName(), new String[]{"--instance", "uno", "-u", "root", "-p", "secret"});

        log.info("Opts:{}", findLockOpts.instance);

    }

    @Test
    public void noLocks() throws Exception {

        String instanceId = UUID.randomUUID().toString();

        Instance instance = createMock(Instance.class);
        expect(instance.getInstanceID()).andReturn(instanceId);

        IZooReaderWriter zrw = createMock(IZooReaderWriter.class);
        expect(zrw.putPersistentData(eq("/accumulo/"+ instanceId + "/fate"), anyObject(), anyObject())).andThrow(new KeeperException.NodeExistsException());
        replay(instance, zrw);

        FindLocks finder = new FindLocks(findLockOpts, instance, zrw);
        finder.execute();

        verify(instance, zrw);
    }

    @Test
    public void hello() throws Exception {
        FindLocks finder = new FindLocks(findLockOpts);
        finder.execute();
    }

    @Test
    public void rtest() {
        String v = "[R:+default, R:2, W:3]";
        // FindLocks.parseLocks(v).forEach(p -> log.info("Parts: {}", FindLocks.parseLockInfo(p)));
    }

}