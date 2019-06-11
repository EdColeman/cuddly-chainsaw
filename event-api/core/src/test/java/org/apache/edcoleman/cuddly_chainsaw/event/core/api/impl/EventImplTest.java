package org.apache.edcoleman.cuddly_chainsaw.event.core.api.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class EventImplTest {

  private static final Logger log = LogManager.getLogger();

  @Test void getTraceId() {

    assertEquals("X", "X");
  }

  @Test
  public void removedTest() {

    Map<String,String> prev = new TreeMap<>();
    Map<String,String> current = new TreeMap<>();

    prev.put("A", "1");
    prev.put("B", "2");

    Diffs result = calcDiff(prev,current);

    assertEquals(0, result.added.size());
    assertEquals(2, result.removed.size());

    log.debug("Added: {}", result.added);
    log.debug("Removed: {}", result.removed);

  }

  @Test
  public void addedTest() {

    Map<String,String> prev = new TreeMap<>();
    Map<String,String> current = new TreeMap<>();

    current.put("A", "1");
    current.put("B", "2");

    Diffs result = calcDiff(prev, current);

    assertEquals(2, result.added.size());
    assertEquals(0, result.removed.size());

    log.debug("Added: {}", result.added);
    log.debug("Removed: {}", result.removed);
  }

  @Test
  public void diffsTest() {

    Map<String,String> prev = new TreeMap<>();
    Map<String,String> current = new TreeMap<>();

    prev.put("A", "1");
    prev.put("B", "2");
    prev.put("D", "4");

    current.put("A", "1");
    current.put("C", "3");
    current.put("D", "4");

    Diffs result = calcDiff(prev,current);

    log.debug("Added: {}", result.added);
    log.debug("Removed: {}", result.removed);

    assertEquals(1, result.added.size());
    assertEquals(1, result.removed.size());

  }

    public static class Diffs {
    Map<String,String> added = new TreeMap<>();
    Map<String,String> removed = new TreeMap<>();
  }

  private Diffs calcDiff(Map<String,String> prev, Map<String,String> current){

    Diffs result = new Diffs();

    Iterator<Map.Entry<String,String>> had = prev.entrySet().iterator();
    Iterator<Map.Entry<String,String>> now = current.entrySet().iterator();

    Map.Entry<String,String> n = null;
    Map.Entry<String,String> h = null;

    while(now.hasNext()){

      n = now.next();

      if(had.hasNext()){

        h = had.next();

        int compare = n.getKey().compareTo(h.getKey());

        if(compare == 0){
          // skip - entry in both.
          continue;
        }

        if (compare < 0){
          while(now.hasNext() && n.getKey().compareTo(h.getKey()) < 0){
            result.added.put(n.getKey(), n.getValue());
            n = now.next();
          }
        } else {
          while(had.hasNext() && n.getKey().compareTo(h.getKey()) > 0) {
            result.removed.put(h.getKey(), h.getValue());
            h = had.next();
          }
        }

      } else {
        result.added.put(n.getKey(), n.getValue());
        break;
      }
    }

    // handle remainders.
    while(now.hasNext()){
      Map.Entry<String,String> e = now.next();
      result.added.put(e.getKey(), e.getValue());
    }

    while(had.hasNext()){
      Map.Entry<String,String> e = had.next();
      result.removed.put(e.getKey(), e.getValue());
    }

    return result;
  }
}
