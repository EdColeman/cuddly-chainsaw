package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl;

import org.apache.accumulo.core.data.TableId;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.BackingStore;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.PropValue;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.TableConfigStore;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.TableProps;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemBackingStore implements TableConfigStore, BackingStore {

  Map<String,PropValue> system = new HashMap<>();
  Map<TableId,TableProps> tableProps = new HashMap<>();

  public InMemBackingStore() {

  }

  public void setProperty(final Scope scope, Optional<TableId> tableId, final String name,
      final PropValue v) {

    switch (scope) {
      case DEFAULT:
        throw new UnsupportedOperationException("default scope not implemented");
      case SYSTEM:
        storeSystemProp(name, v);
        break;
      case TABLE:
        storeTableProp(tableId, name, v);
        break;
      case ITERATOR:
        throw new UnsupportedOperationException("iterator scope not implemented");
      default:
        throw new IllegalStateException("unknown scope");
    }
  }

  private void storeTableProp(Optional<TableId> tableId, String name, PropValue v) {

    Map<String,PropValue> tableProp = tableProps.computeIfAbsent(tableId.get(), tableId1 -> {
      return TableProps.forTableId(tableId1);
    });

    tableProp.put(name, v);
  }

  public Optional<PropValue> getProperty(final TableId tableId, final String name) {

    Map<String,PropValue> tableProp = tableProps.get(tableId);
    if (tableProp != null) {
      PropValue v = tableProp.get(name);
      if (v != null) {
        return Optional.of(v);
      }
    }
    PropValue v = system.get(name);
    if (v == null) {
      return Optional.empty();
    }

    return Optional.of(v);
  }

  public Optional<PropValue> getPropertyOrDefault(final TableId tableId, final String name,
      final PropValue aDefault) {
    return Optional.of(aDefault);
  }

  private void storeSystemProp(final String name, final PropValue v) {
    system.put(name, v);
  }
}
