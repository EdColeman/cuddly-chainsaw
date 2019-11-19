package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl;

import org.apache.accumulo.core.data.TableId;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.PropValue;
import org.apache.edcoleman.cuddly_chainsaw.zktestbed.TableConfigStore;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemBackingStoreTest {

  @Test public void emptyStore(){
    InMemBackingStore store = new InMemBackingStore();
    Optional<PropValue> v = store.getProperty( TableId.of("aaa"), "no.such.prop");
    assertTrue(v.isEmpty());
  }


  @Test public void globalProp(){
    InMemBackingStore store = new InMemBackingStore();

    TableId id = TableId.of("aaa");
    String propName = "a.global.property";
    PropValue v = new PropValue("a.value");

    store.setProperty(TableConfigStore.Scope.SYSTEM, Optional.empty(), propName, v);

    Optional<PropValue> r = store.getProperty( id, propName);
    assertEquals(v, r.get());
  }

  @Test public void tableProp(){
    InMemBackingStore store = new InMemBackingStore();

    TableId id = TableId.of("aaa");
    String propName = "a.global.property";
    PropValue gv = new PropValue("a.value");

    String tablePropName = "a.table.property";
    PropValue tv = new PropValue("t.value");

    store.setProperty(TableConfigStore.Scope.SYSTEM, Optional.empty(), propName, gv);
    store.setProperty(TableConfigStore.Scope.TABLE, Optional.of(id), tablePropName, tv);

    Optional<PropValue> r = store.getProperty( id, tablePropName);
    assertEquals(tv, r.get());
  }

}
