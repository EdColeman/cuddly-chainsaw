package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl;

import org.apache.edcoleman.cuddly_chainsaw.zktestbed.TableConfigStore;

public class TableConfigStoreImpl implements TableConfigStore {

  private static TableConfigStore _instance = null;

  private enum Scope {
    DEFAULT,
    SYSTEM,
    TABLE,
    ITERATOR
  }


  // scope -
  private TableConfigStoreImpl(){

  }

  public static synchronized TableConfigStore getInstance(){
    if(_instance == null){
      _instance = new TableConfigStoreImpl();
    }
    return _instance;
  }

}
