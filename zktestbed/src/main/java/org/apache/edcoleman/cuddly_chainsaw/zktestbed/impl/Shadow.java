package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl;

import org.apache.zookeeper.data.Stat;

import java.util.StringJoiner;

public class Shadow {

  public static final String CONFIG_ROOT = "/accumulo/12345/config";
  public static final String SENTINEL = "sentinel_1";

  public static class Node {

    private final String sentinelPath;
    private final String nodePath;

    private int version = -1;
    private boolean hasChanged = true;

    private Node(final String sentinelPath, final String nodePath) {
      this.sentinelPath = sentinelPath;
      this.nodePath = nodePath;
    }

    public String getSentinelPath(){
      return sentinelPath;
    }

    public String getNodePath() {
      return nodePath;
    }

    public boolean setVersion(final Stat stat){
      if(stat.getVersion() != version){
        hasChanged = true;
        version = stat.getVersion();
      } else {
        hasChanged = false;
      }

      return hasChanged;
    }

    public int getVersion(){
      return version;
    }

    public void clearChanged(){
      hasChanged = false;
    }

    @Override public String toString() {
      return new StringJoiner(", ", Node.class.getSimpleName() + "[", "]")
          .add("nodePath='" + nodePath + "'").add("sentinelPath='" + sentinelPath + "'")
          .add("version=" + version).add("hasChanged=" + hasChanged).toString();
    }
  }

  public static class Factory{

    public static Node create(final String relPath){
      String sentinel = CONFIG_ROOT + "/" + getSentinel(relPath);
      return new Node(sentinel, sentinel + "/" + relPath);
    }

    // TODO place holder for picking sentinel node using the relPath
    public static String getSentinel(final String relPath){
      return SENTINEL;
    }
  }
}
