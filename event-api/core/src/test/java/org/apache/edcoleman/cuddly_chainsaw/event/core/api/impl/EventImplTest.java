package org.apache.edcoleman.cuddly_chainsaw.event.core.api.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventImplTest {

  private static final Logger log = LogManager.getLogger();

  @Test void getTraceId() {

    assertEquals("X", "X");
  }
}
