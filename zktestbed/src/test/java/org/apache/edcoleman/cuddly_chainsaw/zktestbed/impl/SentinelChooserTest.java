package org.apache.edcoleman.cuddly_chainsaw.zktestbed.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

public class SentinelChooserTest {

  private final static Logger log = LogManager.getLogger();

  public final int fanout = 5;

  enum Scope {
    DEFAULT(),
    SITE(),
    SYSTEM(),
    NAMESPACE(),
    TABLE();

    private int nameHashCode;

    Scope(){
      nameHashCode = this.name().hashCode();
    }

    public int getNameHashCode(){
      return nameHashCode;
    }
  }

  private static class KeySample {

    private Scope scope;
    private String id;

    public KeySample(final Scope scope, final String id){
      this.scope = scope;
      this.id = id;
    }

    @Override public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;

      KeySample keySample = (KeySample) o;

      if (scope != keySample.scope)
        return false;
      return id != null ? id.equals(keySample.id) : keySample.id == null;
    }

    @Override public int hashCode() {
      int result = scope != null ? scope.getNameHashCode() : 0;
      result = 31 * result + (id != null ? id.hashCode() : 0);
      return result;
    }
  }

  @Test public void x(){
    log.info("D:{}", Scope.DEFAULT.hashCode());
    log.info("D:{}", Scope.DEFAULT.getNameHashCode());
    log.info("D:{}", "DEFAULT".hashCode());

    log.info("S:{}", Scope.SITE.hashCode());
    log.info("S:{}", Scope.SITE.getNameHashCode());
    log.info("S:{}", "SITE".hashCode());

    log.info("Y:{}", Scope.SYSTEM.hashCode());
    log.info("N:{}", Scope.NAMESPACE.hashCode());
    log.info("T:{}", Scope.TABLE.hashCode());

  }

  @Test
  public void y(){
    KeySample k1 = new KeySample(Scope.DEFAULT, "a");
    log.info("K1:{}", k1.hashCode());

    KeySample k2 = new KeySample(Scope.SITE, "a");
    log.info("K2:{}", k2.hashCode());

  }
}
