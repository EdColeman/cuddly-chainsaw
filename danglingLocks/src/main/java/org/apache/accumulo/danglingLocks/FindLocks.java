package org.apache.accumulo.danglingLocks;

import com.beust.jcommander.Parameter;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FindLocks {

    public static Logger log = LoggerFactory.getLogger(FindLocks.class);

    private final Instance instance;
    private final IZooReaderWriter zrw;

    public FindLocks(FindLockOpts opts, final Instance instance, final IZooReaderWriter zrw){
        this.instance = instance;
        this.zrw = zrw;
    }

    public FindLocks(FindLockOpts opts) {

        ClientConfiguration clientConfig = ClientConfiguration.loadDefault();

        instance = opts.getInstance();

        zrw = new ZooReaderWriterFactory().getZooReaderWriter(
                instance.getZooKeepers(), instance.getZooKeepersSessionTimeOut(), "uno");
    }

    public void execute() throws InterruptedException, KeeperException {

        AdminUtil<String> admin = new AdminUtil<>(false);

        ZooStore<String> zs = new ZooStore<>(ZooUtil.getRoot(instance) + Constants.ZFATE, zrw);

        AdminUtil.FateStatus fateStatus = admin.getStatus(zs, zrw,
                ZooUtil.getRoot(instance) + Constants.ZTABLE_LOCKS, null, null);

        List<String> currLocks = zrw.getChildren(ZooUtil.getRoot(instance) + Constants.ZTABLE_LOCKS);
        log.debug("current zoo lock parents: {}", currLocks);

        log.debug("transactions: {}", fateStatus.getTransactions());
        log.debug("locks held: {}", fateStatus.getDanglingHeldLocks());
        log.debug("locks waiting: {}", fateStatus.getDanglingWaitingLocks());

        if(log.isDebugEnabled()) {
            fateStatus.getDanglingHeldLocks().forEach((k, v) -> log.trace("DH: txid:{}, lock info:{}", k, v));
            fateStatus.getDanglingWaitingLocks().forEach((k, v) -> log.trace("WH: txid:{}, lock info:{}", k, v));
        }

        Set<String> canDelete = getDanglingLocks(fateStatus.getDanglingHeldLocks());
        canDelete.addAll(getDanglingLocks(fateStatus.getDanglingWaitingLocks()));

        System.out.println("Candidate FATE locks for deletion");
        canDelete.forEach(System.out::println);
    }
    private Set<String> getDanglingLocks(Map<String, List<String>> fateLocks) {
        
        Set<Id> txIds = new HashSet<>();
        List<Id> lockIds = parseTxLocks(fateLocks, txIds);

        Set<String> canDelete = new TreeSet<>();

        String basePath = ZooUtil.getRoot(instance) + Constants.ZTABLE_LOCKS;

        for (Id lid : lockIds) {
            try {
                String nodePath = basePath + "/" + lid.getId();
                List<String> lockParent = zrw.getChildren(nodePath);
                log.trace("lock nodes: {}", lockParent);
                for (String parent : lockParent) {
                    String lockPath = nodePath + "/" + parent;
                    Pair<String, Id> txId = parseLockInfo(new String(zrw.getData(lockPath, null), UTF_8));
                    log.trace("Looking for: {}, data {}", lockPath, txId);
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
        FindLockOpts opts = new FindLockOpts();
        opts.parseArgs(FindLocks.class.getName(), args);

        FindLocks findLocks = new FindLocks(opts);
        findLocks.execute();
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
    public static class FindLockOpts extends ClientOpts {
        @Parameter(
                names = {"-v", "--verbose"},
                description = "print additional information during execution"
        )
        boolean verbose = false;

        @Parameter(
                names = {"--deleteFromZooKeeper"},
                description = "delete found dangling locks from ZooKeeper"
        )
        boolean deleteFromZk = false;
    }
}
