package org.apache.edcoleman.cuddly_chainsaw.top_k;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

class TopKTest {

    @Test
    public void emptyTest() {

        TopK topK = new TopK();

        BigInteger seed = BigInteger.valueOf(961834403L);
        SecureRandom r = new SecureRandom( );

        byte[] bytes = new byte[Integer.BYTES];

        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);

        for(int x = 0;  x < 16; x++){
            r.nextBytes(bytes);
            buffer.put(bytes,0,bytes.length);
            buffer.flip();
            System.out.println(String.format("0x%08X", new Integer(buffer.getInt())));
            buffer.clear();
        }
    }


}

