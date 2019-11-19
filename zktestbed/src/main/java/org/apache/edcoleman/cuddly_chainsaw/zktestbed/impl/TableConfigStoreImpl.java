package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl;

import org.apache.accumulo.core.data.TableId;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.BackingStore;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.ConfigReader;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.ConfigWriter;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.PropValue;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.TableConfigStore;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class TableConfigStoreImpl implements TableConfigStore, ConfigWriter, ConfigReader {

  private static TableConfigStore _instance = null;
  private BackingStore store = null;

  // scope -
  private TableConfigStoreImpl(){
  }

  public static synchronized TableConfigStore getInstance(){
    if(_instance == null){
      _instance = new TableConfigStoreImpl();
    }
    return _instance;
  }

  // TODO inject or set at creation, maybe with a builder?
  public void setBackingStore(final BackingStore store){
    this.store = store;
  }

  @Override public Optional<PropValue> getProperty(TableId tableId, String name) {
    return Optional.empty();
  }

  @Override public boolean haveProperty(TableId tableId, String name) {
    return false;
  }

  @Override public boolean isTableProperty(String name) {
    return false;
  }

  @Override public boolean isSystemProperty(String name) {
    return false;
  }

  @Override public boolean isDefaultProperty(String name) {
    return false;
  }

  @Override public Map<String,PropValue> getProperties(TableId tableId, Predicate<String> filter) {
    return null;
  }

  @Override public boolean setGlobalProperty(String name, PropValue value) {
    return false;
  }

  @Override public boolean updateGlobalProperty(String name, PropValue current, PropValue update) {
    return false;
  }

  @Override public boolean setProperty(TableId tableId, String name, PropValue value) {
    return false;
  }

  @Override
  public boolean updateProperty(TableId tableId, String name, PropValue current, PropValue update) {
    return false;
  }

  @Override public boolean setGlobalProperties(Map<String,PropValue> propMap) {
    return false;
  }

  @Override public boolean setProperties(TableId tableId, Map<String,PropValue> propMap) {
    return false;
  }
}
