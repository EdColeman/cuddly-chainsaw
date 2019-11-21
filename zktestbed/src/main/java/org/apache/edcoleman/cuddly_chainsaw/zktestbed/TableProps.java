package org.apache.edcoleman.cuddly_chainsaw.zktestbed;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.accumulo.core.data.TableId;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TableProps implements Map<String,PropValue> {

  private final PropScope scope;
  private final TableId tableId;
  private final Map<String,PropValue> delegateMap = new HashMap<>();

  private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
  private final ReentrantReadWriteLock.ReadLock rlock = rwLock.readLock();
  private final ReentrantReadWriteLock.WriteLock wlock = rwLock.writeLock();

  private TableProps(final PropScope scope, final TableId tableId) {
    this.scope = scope;
    this.tableId = tableId;
  }

  public static TableProps forTableId(final TableId tableId) {
    return new TableProps(PropScope.TABLE, tableId);
  }

  public static TableProps forSystem() {
    return new TableProps(PropScope.SYSTEM, null);
  }

  @Override public int size() {
    rlock.lock();
    try {
      return delegateMap.size();
    } finally {
      rlock.unlock();
    }
  }

  @Override public boolean isEmpty() {
    rlock.lock();
    try {
      return delegateMap.isEmpty();
    } finally {
      rlock.unlock();
    }
  }

  @Override public boolean containsKey(Object key) {
    rlock.lock();
    try {
      return delegateMap.containsKey(key);
    } finally {
      rlock.unlock();
    }
  }

  @Override public boolean containsValue(Object value) {
    rlock.lock();
    try {
      return delegateMap.containsValue(value);
    } finally {
      rlock.unlock();
    }
  }

  @Override public PropValue get(Object key) {
    rlock.lock();
    try {
      return delegateMap.get(key);
    } finally {
      rlock.unlock();
    }
  }

  @Override public PropValue put(String key, PropValue value) {
    wlock.lock();
    try {
      return delegateMap.put(key, value);
    } finally {
      wlock.unlock();
    }
  }

  @Override public PropValue remove(Object key) {
    wlock.lock();
    try {
      return delegateMap.remove(key);
    } finally {
      wlock.unlock();
    }
  }

  @Override public void putAll(@NonNull Map<? extends String,? extends PropValue> m) {
    wlock.lock();
    try {
      delegateMap.putAll(m);
    } finally {
      wlock.unlock();
    }
  }

  @Override public void clear() {
    wlock.lock();
    try {
      delegateMap.clear();
    } finally {
      wlock.unlock();
    }
  }

  @Override @NonNull public Set<String> keySet() {
    rlock.lock();
    try {
      return Collections.unmodifiableSet(delegateMap.keySet());
    } finally {
      rlock.unlock();
    }
  }

  @Override @NonNull public Collection<PropValue> values() {
    rlock.lock();
    try {
      return Collections.unmodifiableCollection(delegateMap.values());
    } finally {
      rlock.unlock();
    }
  }

  @Override @NonNull public Set<Entry<String,PropValue>> entrySet() {
    rlock.lock();
    try {
      return Collections.unmodifiableSet(delegateMap.entrySet());
    } finally {
      rlock.unlock();
    }
  }

  @Override public String toString() {
    rlock.lock();
    try {
      return new StringJoiner(", ", TableProps.class.getSimpleName() + "[", "]")
          .add("scope=" + scope).add("tableId=" + tableId).add("delegateMap=" + delegateMap)
          .toString();
    } finally {
      rlock.unlock();
    }
  }

  private static class SerDes {
    private final static Gson gson = new Gson();
    private static final TypeToken<Map<String,PropValue>> gsonDataType = new TypeToken<>() {
    };
  }
}
