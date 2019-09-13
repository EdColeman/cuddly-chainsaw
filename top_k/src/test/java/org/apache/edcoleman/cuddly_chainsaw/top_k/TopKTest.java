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

        topK.add(new Text("1"));
        topK.add(new Text("1"));
        topK.add(new Text("1"));
        topK.add(new Text("2"));
        topK.add(new Text("2"));
        topK.add(new Text("3"));
        topK.add(new Text("4"));
        topK.add(new Text("5"));
        topK.add(new Text("6"));
        topK.add(new Text("7"));
        topK.add(new Text("8"));
        topK.add(new Text("9"));
        topK.add(new Text("10"));
        topK.add(new Text("11"));
        topK.add(new Text("12"));
        topK.add(new Text("13"));
        topK.add(new Text("14"));

        log.debug("Top: {}", topK.getTopK());
    }
}

