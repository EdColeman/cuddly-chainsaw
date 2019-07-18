package org.apache.edcoleman.cuddly_chainsaw.iterators;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class RangeTest {

  @Test public void simple(){

    String a = "a";
    String a0 = "a\u0000";
    String ae = "a~";
    String t = "~";
    String te = "~\u0000";
    String tt = "~~";
    String ta = "~a";

    System.out.println("a len: " + a.length());
    System.out.println("a0 len: " + a0.length());
    System.out.println("a~ len: " + ae.length());

    System.out.println("a -> a0: " + a.compareTo(a0));
    System.out.println("a -> a~: " + a.compareTo(ae));
    System.out.println("a0 -> a~: " + a0.compareTo(ae));

    Set<String> sorted = new TreeSet<>();
    sorted.add(a);
    sorted.add(a0);
    sorted.add(ae);
    sorted.add(t);
    sorted.add(te);
    sorted.add(tt);
    sorted.add(ta);

    System.out.println("S: " + sorted);

    Key key1 = new Key();

    Range r1 = new Range("a", "z");

    // r1.beforeStartKey()
  }
}
