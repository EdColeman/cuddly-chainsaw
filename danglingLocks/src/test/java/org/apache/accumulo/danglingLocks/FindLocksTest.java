package org.apache.accumulo.danglingLocks;

import org.apache.accumulo.core.cli.ClientOpts;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FindLocksTest {

    Logger log = LoggerFactory.getLogger(FindLocksTest.class);

    @Test
    public void hello() throws Exception {
        ClientOpts opts = new ClientOpts();
        opts.parseArgs(FindLocks.class.getName(), new String[]{"-u", "root", "-p", "secret"});
        FindLocks finder = new FindLocks(opts);
    }

    @Test
    public void rtest() {
        String v = "[R:+default, R:2, W:3]";
        // FindLocks.parseLocks(v).forEach(p -> log.info("Parts: {}", FindLocks.parseLockInfo(p)));
    }

}