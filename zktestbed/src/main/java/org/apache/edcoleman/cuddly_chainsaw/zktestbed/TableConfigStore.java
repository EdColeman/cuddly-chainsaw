package org.apache.edcoleman.cuddly_chainsaw.zktestbed;

public interface TableConfigStore {

  enum Scope {
    DEFAULT,
    SYSTEM,
    TABLE,
    ITERATOR
  }

}
