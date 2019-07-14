package org.apache.edcoleman.drop_util.control.rules;

import org.apache.edcoleman.drop_util.control.Blackboard;
import org.apache.edcoleman.drop_util.control.BlackboardImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RootTest {

    private static Logger log = LogManager.getLogger();

    @Test public void simple(){

        Blackboard blackboard = BlackboardImpl.getInstance();

        Root r = new Root(blackboard);

        HealthCheck hc = new HealthCheck();

        r.addNode(hc);

        log.debug("Apply: {}", r.apply());
    }
}
