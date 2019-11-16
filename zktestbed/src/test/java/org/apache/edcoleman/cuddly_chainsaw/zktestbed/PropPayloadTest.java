package org.apache.edcoleman.cuddly_chainsaw.zktestbed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PropPayloadTest {

  private final static Logger log = LogManager.getLogger();

  @Test void emptyRoundTrip() {
    PropPayload payload = new PropPayload();
    byte[] serialized = payload.toBytes();

    assertTrue(serialized.length > 0);

    log.debug("Empty len: {}", serialized.length);

    PropPayload p2 =PropPayload.fromBytes(serialized);

    assertNotNull(p2);

    log.debug("emoty: {}", p2);

  }


  @Test void roundTrip() {
    PropPayload payload = new PropPayload();

    payload.addProp("a.b.c.1", new PropValue("v-a.b.c.1"));
    payload.addProp("a.b.c.2", new PropValue("v-a.b.c.2"));
    payload.addProp("a.b.c.3", new PropValue("v-a.b.c.3"));
    payload.addProp("a.b.c.4", new PropValue("v-a.b.c.4"));
    payload.addProp("a.b.c.5", new PropValue("v-a.b.c.5"));

    byte[] serialized = payload.toBytes();
    assertTrue(serialized.length > 0);

    log.debug("len len: {}", serialized.length);

    PropPayload p2 = PropPayload.fromBytes(serialized);

    log.debug("RT: {}", p2);
  }


  @Test void compressed() {
    PropPayload payload = new PropPayload();

    payload.addProp("a.b.c.1", new PropValue("v-a.b.c.1"));
    payload.addProp("a.b.c.2", new PropValue("v-a.b.c.2"));
    payload.addProp("a.b.c.3", new PropValue("v-a.b.c.3"));
    payload.addProp("a.b.c.4", new PropValue("v-a.b.c.4"));
    payload.addProp("a.b.c.5", new PropValue("v-a.b.c.5"));

    byte[] serialized = payload.toBytes();

    log.info("S len: {}", serialized.length);

    PropPayload p2 = PropPayload.fromBytes(serialized);

    log.debug("RT: {}", p2);
  }
}
