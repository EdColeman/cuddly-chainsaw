package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl.protocol;

import org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl.ZkMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class ZkState {

  private final static Logger log = LogManager.getLogger();

  private static final String zkPathBase = "/accumulo/123/config";

  private static class ZkStateCtx {

    private final ZooKeeper zooKeeper;
    private final ZkMap backingMap;
    private Optional<KeeperException> keeperException;

    private final AtomicInteger zkConnectionErrors = new AtomicInteger();
    private final AtomicInteger zkNodeErrors = new AtomicInteger();

    public ZkStateCtx(final ZooKeeper zooKeeper, final ZkMap backingMap) {
      this.zooKeeper = zooKeeper;
      this.backingMap = backingMap;
    }

    public void setZkException(KeeperException ex) {
      keeperException = Optional.of(ex);
    }
  }

  private static class ZkUpdateProtocol {

    private final ZkStateCtx ctx;
    private ZkStateProtocol next;

    public ZkUpdateProtocol(final ZooKeeper zooKeeper, final ZkMap zkMap) {
      this.ctx = new ZkStateCtx(zooKeeper, zkMap);
    }

    public void update(final String name, final String value) {
      next = new CheckVersion();
      next.handle(ctx);
    }

    public boolean handle(final ZkStateCtx ctx) {
      return false;
    }

  }

  private interface ZkStateProtocol {
    boolean handle(final ZkStateCtx ctx);
  }

  private static class CheckVersion implements ZkStateProtocol {

    @Override public boolean handle(ZkStateCtx ctx) {
      try {
        Stat stat = new Stat();
        byte[] data = ctx.zooKeeper
            .getData(zkPathBase + "/" + ctx.backingMap.getTableId(), false, stat);

        boolean same = checkVersion(stat, ctx);
        if (!same) {
          ctx.backingMap.update(data);
          ctx.backingMap.setZNodeVersion(stat.getVersion());
        }

        log.info("Stat: {}", stat);
        return false;
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      } catch (KeeperException ex) {
        ctx.setZkException(ex);
      }
      return false;
    }

    boolean checkVersion(final Stat stat, final ZkStateCtx ctx) {
      return stat.getVersion() == ctx.backingMap.getVersion();
    }
  }

  private static class ZkExceptionHandler implements ZkStateProtocol {

    @Override public boolean handle(ZkStateCtx ctx) {
      return false;
    }
  }

  private void create(final ZooKeeper zoo, String path) {
    try {
      zoo.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    } catch (KeeperException.NodeExistsException ex) {
      return;
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    } catch (KeeperException ex) {
      throw new IllegalStateException(ex);
    }
  }
}
