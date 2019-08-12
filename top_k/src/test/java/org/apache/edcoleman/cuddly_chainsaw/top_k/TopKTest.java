package org.apache.edcoleman.cuddly_chainsaw.top_k;

import org.apache.hadoop.io.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

class TopKTest {
    private final Logger log = LogManager.getLogger();
    @Test
    public void emptyTest() {

        TopK topK = new TopK();

        topK.add(new Text("1"), new Text(""));
        topK.add(new Text("1"), new Text(""));
        topK.add(new Text("1"), new Text(""));
        topK.add(new Text("2"), new Text(""));
        topK.add(new Text("2"), new Text(""));
        topK.add(new Text("3"), new Text(""));
        topK.add(new Text("4"), new Text(""));
        topK.add(new Text("5"), new Text(""));
        topK.add(new Text("6"), new Text(""));
        topK.add(new Text("7"), new Text(""));
        topK.add(new Text("8"), new Text(""));
        topK.add(new Text("9"), new Text(""));
        topK.add(new Text("10"), new Text(""));
        topK.add(new Text("11"), new Text(""));
        topK.add(new Text("12"), new Text(""));
        topK.add(new Text("13"), new Text(""));
        topK.add(new Text("14"), new Text(""));

        log.debug("Top: {}", topK.getTopK());
    }
}

