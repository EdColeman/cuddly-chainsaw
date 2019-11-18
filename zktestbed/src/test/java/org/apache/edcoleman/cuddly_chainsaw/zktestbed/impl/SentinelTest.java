package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class SentinelTest {
  private final static Logger log = LogManager.getLogger();

  public static final String ALPHABET =
      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

  public static final int BASE = ALPHABET.length();

  @Test void pick() {
    Map<String,Integer> m = new HashMap<>();
    Sentinel sentinel = new Sentinel();

    for (int i = 0; i < 1_000_000; i++) {
      String v = base62Encode(i);
      String n = sentinel.pick(v);
      m.merge(n, 1, Integer::sum);
    }

    for (Map.Entry<String,Integer> e : m.entrySet()) {
      log.info("{} : {}", e.getKey(), e.getValue());
    }

  }

  private String base62Encode(long value) {
    StringBuilder sb = new StringBuilder();
    while (value != 0) {
      sb.append(ALPHABET.charAt((int) (value % 62)));
      value /= 62;
    }
    while (sb.length() < 6) {
      sb.append(0);
    }
    return sb.reverse().toString();
  }
}
