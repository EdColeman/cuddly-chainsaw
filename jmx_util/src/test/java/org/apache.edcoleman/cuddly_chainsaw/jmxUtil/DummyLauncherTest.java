package org.apache.edcoleman.cuddly_chainsaw.jmxUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DummyLauncherTest {

  private final Logger log = LogManager.getLogger();

  @Test public void launcher() throws Exception {

    Runnable runner = () -> {
      log.info("Runner Before");
      try {
        DummyLauncher.exec(DummyMetrics.class);
      } catch (Exception ex){
        log.error("Runner failed with exception", ex);
      }
      log.info("Runner After");
    };

    log.info("Before");
    Thread t = new Thread(runner);
    t.start();
    log.info("After");

    t.join();
  }
}
