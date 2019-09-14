package org.apache.edcoleman.cuddly_chainsaw.jmxUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DummyMetrics {

  private static final Logger log = LogManager.getLogger();

  public DummyMetrics(){

    int ticks = 10;

    while(ticks-- > 0){
      try {
        log.debug("tick: {}", ticks);
        Thread.sleep(5_000);
      }catch(InterruptedException ex){
        Thread.currentThread().interrupt();
        return;
      }
    }
  }

  public static void main(String ... argv){
    log.info("Called main");
    new DummyMetrics();
  }
}
