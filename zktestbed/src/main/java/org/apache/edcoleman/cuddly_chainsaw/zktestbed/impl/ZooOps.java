package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.Optional;
import java.util.StringJoiner;

/**
 * holding class for simplified zoo utilities until production ready versions swapped in.
 */
public class ZooOps {

  private ZooOps() {
    // prevent instantiation of class.
  }

  /**
   * Provide a more friendly toString for zookeeper Stat nodes.
   *
   * @param stat a zookeeper node stat
   * @return a formatted string.
   */
  public static String prettyStat(final Stat stat) {
    return new StringJoiner(", ", "Stat[", "]").add("czxid=" + stat.getCzxid())
        .add("mzxid=" + stat.getMzxid()).add("ctime=" + stat.getCtime())
        .add("mtime=" + stat.getMtime()).add("version=" + stat.getVersion())
        .add("cversion=" + stat.getCversion()).add("aversion=" + stat.getAversion())
        .add("ephemeralOwner=" + stat.getEphemeralOwner()).add("dataLength=" + stat.getDataLength())
        .add("numChildren=" + stat.getNumChildren()).add("pzxid=" + stat.getPzxid()).toString();
  }

  /**
   * Create a zookeeper node - ignore node exists errors.
   *
   * @param zoo  zookeeper client
   * @param path the node path
   * @return true is node created, false if exists.
   */
  public static boolean create(final ZooKeeper zoo, String path) {
    try {
      zoo.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
      return true;
    } catch (KeeperException.NodeExistsException ex) {
      return false;
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      return false;
    } catch (KeeperException ex) {
      throw new IllegalStateException(ex);
    }
  }

  public static Optional<Stat> exists(final ZooKeeper zoo, String nodePath) {
    try {
      return Optional.ofNullable(zoo.exists(nodePath, false));
    } catch (KeeperException e) {
      throw new IllegalStateException("Error calling exits for \'" + nodePath + "\'", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return Optional.empty();
    }
  }
}
