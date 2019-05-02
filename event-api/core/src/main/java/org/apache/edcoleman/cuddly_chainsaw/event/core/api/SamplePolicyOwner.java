package org.apache.edcoleman.cuddly_chainsaw.event.core.api;

public class SamplePolicyOwner {

  private String ownerName;
  private String level; // summary, normal, debug

  public static class Condition {
    private SamplePolicy.SamplePolicyTypes types;

  }
}
