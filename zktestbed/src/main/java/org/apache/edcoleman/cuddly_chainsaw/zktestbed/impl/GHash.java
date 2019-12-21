package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Example using guava murmur hash.
 */
public class GHash {

  private final static Logger log = LogManager.getLogger();

  private int fanout = 4;
  private int depth = 2;

  public GHash(){

  }

  public void x(){
    HashFunction hf = Hashing.murmur3_128(0);

    List<String> s = new ArrayList<>();
    s.add("a");
    s.add("b");
    s.add("c");
    s.add("d");
    s.add("e");
    s.add("f");
    s.add("g");
    s.add("h");

    for(String c : s) {
      HashCode hc = hf.newHasher().putString(c, StandardCharsets.UTF_8).hash();

      int b = hc.asInt() & 0x03;
      // int b = Hashing.consistentHash(hc, 4);

      log.info("C: {}, b: {}", c, b);
    }
  }
}
