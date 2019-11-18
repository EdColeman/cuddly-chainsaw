package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl;

import org.apache.hadoop.util.hash.Hash;
import org.apache.hadoop.util.hash.MurmurHash;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class Sentinel {

  public static final int FAN_OUT = 4;

  private final Hash hasher = MurmurHash.getInstance();

  private final int[] seeds;
  private final String[] names;

  public Sentinel() {

    seeds = new int[FAN_OUT];
    names = new String[FAN_OUT];

    SecureRandom r = new SecureRandom();
    for (int i = 0; i < seeds.length; i++) {
      seeds[i] = r.nextInt();
      names[i] = this.getClass().getSimpleName() + "_" + i;
    }
  }

  public String pick(final String id) {
    int highScore = Integer.MIN_VALUE;
    int candidate = 0;
    byte[] b = id.getBytes(StandardCharsets.UTF_8);
    for(int i = 0; i < FAN_OUT; i++){
      int h = hasher.hash(b,b.length,seeds[i]);
      if(h >= highScore){
        candidate = i;
        highScore = h;
      }
    }
    return names[candidate];
  }
}
