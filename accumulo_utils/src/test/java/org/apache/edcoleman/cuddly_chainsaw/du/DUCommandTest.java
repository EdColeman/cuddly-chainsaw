package org.apache.edcoleman.cuddly_chainsaw.du;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DUCommandTest {

  private static Logger log = LogManager.getLogger();

  @Test void humanReadableByteCount() {

    assertAll("test suffix",
        () -> assertTrue(DUCommand.humanReadableByteCount(100).endsWith("B")),
        () -> assertTrue(DUCommand.humanReadableByteCount(1023).endsWith("B")),
        () -> assertTrue(DUCommand.humanReadableByteCount(1024).endsWith("KB")),
        () -> assertTrue(DUCommand.humanReadableByteCount((1024 * 1024) - 1024).endsWith("KB")),
        () -> assertTrue(DUCommand.humanReadableByteCount(1024 * 1024).endsWith("MB")),
        () -> assertTrue(DUCommand.humanReadableByteCount(1024 * 1024 * 1024 - 1).endsWith("MB")),
        () -> assertTrue(DUCommand.humanReadableByteCount(1024 * 1024 * 1024).endsWith("GB")),
        () -> assertTrue(
            DUCommand.humanReadableByteCount(1024L * 1024 * 1024 * 1024 - 1).endsWith("GB")),
        () -> assertTrue(
            DUCommand.humanReadableByteCount(1024L * 1024 * 1024 * 1024).endsWith("TB")));
  }
}
