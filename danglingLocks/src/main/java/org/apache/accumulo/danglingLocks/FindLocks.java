package org.apache.accumulo.danglingLocks;

import org.apache.accumulo.core.Constants;
import org.apache.accumulo.core.cli.ClientOpts;
import org.apache.accumulo.core.client.ClientConfiguration;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.util.Pair;
import org.apache.accumulo.core.zookeeper.ZooUtil;
import org.apache.accumulo.fate.AdminUtil;
import org.apache.accumulo.fate.ZooStore;
import org.apache.accumulo.fate.zookeeper.IZooReaderWriter;
import org.apache.accumulo.server.zookeeper.ZooReaderWriterFactory;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FindLocks {

    public static Logger log = LoggerFactory.getLogger(FindLocks.class);

    private final Instance instance;
    private final IZooReaderWriter zrw;

    public FindLocks(ClientOpts opts) throws InterruptedException, KeeperException {

        ClientConfiguration clientConfig = ClientConfiguration.loadDefault();

        log.info("Config: {}", clientConfig.getKeys());

        instance = new ZooKeeperInstance(clientConfig);

        AdminUtil<String> admin = new AdminUtil<>(false);

        log.info("Admin: {}", admin);
        zrw = new ZooReaderWriterFactory().getZooReaderWriter(
                instance.getZooKeepers(), instance.getZooKeepersSessionTimeOut(), "uno");

        ZooStore<String> zs = new ZooStore<>(ZooUtil.getRoot(instance) + Constants.ZFATE, zrw);

        AdminUtil.FateStatus fateStatus = admin.getStatus(zs, zrw,
                ZooUtil.getRoot(instance) + Constants.ZTABLE_LOCKS, null, null);

        log.info("STATUS: {}", fateStatus.getTransactions());
        log.info("STATUS held: {}", fateStatus.getDanglingHeldLocks());
        log.info("STATUS waiting: {}", fateStatus.getDanglingWaitingLocks());

        fateStatus.getDanglingHeldLocks().forEach((k, v) -> log.info("H: k:{}, v:{}", k, v));

        Map<String, List<String>> h = fateStatus.getDanglingHeldLocks();

        Set<Id> txIds = new HashSet<>();
        List<Id> lockIds = parseTxLocks(fateStatus.getDanglingHeldLocks(), txIds);

        log.info("TX IDS: {}", txIds);
        log.info("LK IDS: {}", lockIds);

        Set<String> canDelete = getDanglingLocks(lockIds, txIds);

        log.info("DELETE ME :{}", canDelete);

        List<String> currLocks = zrw.getChildren(ZooUtil.getRoot(instance) + Constants.ZTABLE_LOCKS);
        log.info("zoo locks: {}", currLocks);

    }

    private Set<String> getDanglingLocks(final List<Id> lockIds, Set<Id> txIds) {
        Set<String> canDelete = new HashSet<>();

        String basePath = ZooUtil.getRoot(instance) + Constants.ZTABLE_LOCKS;

        for (Id lid : lockIds) {
            try {
                String nodePath = basePath + "/" + lid.getId();
                List<String> lockParent = zrw.getChildren(nodePath);
                log.info("lock nodes: {}", lockParent);
                for (String parent : lockParent) {
                    String lockPath = nodePath + "/" + parent;
                    log.info("Looking for: {}", lockPath);
                    Pair<String, Id> txId = parseLockInfo(new String(zrw.getData(lockPath, null), UTF_8));
                    log.info("DATA: {} ", txId);
                    if (txId != null && txIds.contains(txId.getSecond())) {
                        canDelete.add(lockPath);
                    }
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted reading ZooKeeper", ex);
            } catch (KeeperException.NoNodeException ex) {
                log.info("skipping lock node, it no longer exists in ZooKeeper", ex);
            } catch (KeeperException ex) {
                log.info("skipping lock node, it because of ZooKeeper exception", ex);
            }
        }
        return canDelete;
    }

    private List<Id> parseTxLocks(Map<String, List<String>> danglingLocks, Set<Id> txIds) {
        List<Id> nodeNames = new ArrayList<>();
        for (Map.Entry<String, List<String>> e : danglingLocks.entrySet()) {
            txIds.add(new Id(e.getKey()));
            List<String> lockedIdList = e.getValue();
            for (String lockString : lockedIdList) {
                Pair<String, Id> lockInfo = parseLockInfo(lockString);
                if (lockInfo != null) {
                    nodeNames.add(lockInfo.getSecond());
                }
            }
        }
        return nodeNames;
    }

    /**
     * Parse the lock info string and return the lock type (read, write) and the table id as a Pair of strings.
     */
    private Pair<String, Id> parseLockInfo(final String lockString) {
        String[] parts = lockString.trim().split(":");
        if (parts.length == 2) {
            return new Pair<>(parts[0], new Id(parts[1]));
        }
        return null;
    }

    public static void main(String... args) throws Exception {

        ClientOpts opts = new ClientOpts();
        opts.parseArgs(FindLocks.class.getName(), args);

        new FindLocks(opts);
    }

    private static class Id {
        final String id;

        public Id(final String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id oId = (Id) o;
            return id.equals(oId.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "Id{id='" + id + "'}";
        }
    }
}
