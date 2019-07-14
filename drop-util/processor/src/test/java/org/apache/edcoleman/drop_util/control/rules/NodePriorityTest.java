package org.apache.edcoleman.drop_util.control.rules;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class NodePriorityTest {

    private static Logger log = LogManager.getLogger();

    @Test public void x(){
        log.info("{}", Integer.compare(1,2));

        Set<Integer> ts = new TreeSet<>();

        ts.add(1);
        ts.add(99);
        ts.add(50);

        log.info("ts: {}", ts);
    }
}
