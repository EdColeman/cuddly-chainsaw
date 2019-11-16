package org.apache.edcoleman.cuddly_chainsaw.zktestbed;

import org.apache.zookeeper.data.Stat;

public interface ConfigAdmin {

  String getPropSentinel( final String name);

}
