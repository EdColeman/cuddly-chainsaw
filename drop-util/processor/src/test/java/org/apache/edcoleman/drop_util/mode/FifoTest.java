package org.apache.edcoleman.drop_util.mode;

import org.apache.edcoleman.drop_util.message.TableRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class FifoTest {

    private static Logger log = LogManager.getLogger();

    @Test
    void emptySet() {


        // Fifo fifo = new Fifo(tableSet);


    }


    @Test
    void oneEntry() {

        TableSet candidates = new TableSet();

        TableRecord r1 = new TableRecord.Builder("a").withNumTablets(10).withSize(1_000).build();
        candidates.add(r1);

        Fifo fifo = new Fifo(candidates.OrderedByOldestView());

        assertAll("one entry",
                () -> assertTrue(fifo.hasNext()),
                () -> assertEquals(r1, fifo.next()),
                () -> assertFalse(fifo.hasNext())
        );

    }

    @Test
    void twoEntries() {

        TableSet candidates = new TableSet();

        TableRecord r1 = new TableRecord.Builder("second").withNumTablets(10).withSize(1_000).build();
        candidates.add(r1);

        TableRecord r2 = new TableRecord.Builder("third").queuedAt(r1.getQueued().plusMillis(360_000)).withNumTablets(10).withSize(1_000).build();
        candidates.add(r2);

        TableRecord r3 = new TableRecord.Builder("first").queuedAt(r1.getQueued().minusMillis(360_000)).withNumTablets(10).withSize(1_000).build();
        candidates.add(r3);

        Fifo fifo = new Fifo(candidates.OrderedByOldestView());

        assertAll("multiple entries",
                () -> assertTrue(fifo.hasNext()),
                () -> {
                    Instant prev = Instant.MIN;
                    while (fifo.hasNext()) {
                        Instant queued = fifo.next().getQueued();
                        log.info("Prev {}, Queued {} is {}", prev, queued, prev.isBefore(queued));
                        assertTrue(prev.isBefore(queued));
                        prev = queued;

                    }
                },
                () -> assertFalse(fifo.hasNext())
        );

    }
}
