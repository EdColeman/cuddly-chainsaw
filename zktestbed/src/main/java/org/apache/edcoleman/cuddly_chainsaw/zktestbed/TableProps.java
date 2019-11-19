package org.apache.edcoleman.cuddly_chainsaw.zktestbed;

import org.apache.accumulo.core.data.TableId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public class TableProps implements Map<String, PropValue> {

  private final TableConfigStore.Scope scope;
  private final TableId tableId;
  private final Map<String,PropValue> delegateMap = new HashMap<>();

  private TableProps(final TableConfigStore.Scope scope, final TableId tableId){
    this.scope = scope;
    this.tableId = tableId;
  }

  public static TableProps forTableId(final TableId tableId){
    return new TableProps(TableConfigStore.Scope.TABLE, tableId);
  }

  public static TableProps forSystem(){
    return new TableProps(TableConfigStore.Scope.SYSTEM, null);
  }

  @Override public int size() {
    return delegateMap.size();
  }

  @Override public boolean isEmpty() {
    return delegateMap.isEmpty();
  }

  @Override public boolean containsKey(Object key) {
    return delegateMap.containsKey(key);
  }

  @Override public boolean containsValue(Object value) {
    return delegateMap.containsValue(value);
  }

  @Override public PropValue get(Object key) {
    return delegateMap.get(key);
  }

  @Override public PropValue put(String key, PropValue value) {
    return delegateMap.put(key, value);
  }

  @Override public PropValue remove(Object key) {
    return delegateMap.remove(key);
  }

  @Override public void putAll(Map<? extends String,? extends PropValue> m) {
    delegateMap.putAll(m);
  }

  @Override public void clear() {
    delegateMap.clear();
  }

  @Override public Set<String> keySet() {
    return delegateMap.keySet();
  }

  @Override public Collection<PropValue> values() {
    return delegateMap.values();
  }

  @Override public Set<Entry<String,PropValue>> entrySet() {
    return delegateMap.entrySet();
  }

  @Override public String toString() {
    return new StringJoiner(", ", TableProps.class.getSimpleName() + "[", "]").add("scope=" + scope)
        .add("tableId=" + tableId).add("delegateMap=" + delegateMap).toString();
  }
}
