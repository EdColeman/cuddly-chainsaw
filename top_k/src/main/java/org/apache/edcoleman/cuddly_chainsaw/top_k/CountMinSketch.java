/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.edcoleman.cuddly_chainsaw.top_k;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.hash.Hash;
import org.apache.hadoop.util.hash.MurmurHash;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class CountMinSketch {

    private int numBuckets;
    private int bucketMask;

    private int numHashers;

    private long numInserts = 0;

    private long[][] counters;

    // some randomly generated seed values
    private static final int seeds[] = {
            0xA329F677,
            0x4DFBB2CC,
            0x5DF7668E,
            0xB82063BD,
            0xFF5A07F4,
            0x9876F861,
            0xC52A629A,
            0x9A61A231,
            0x10025AF1,
            0xF4403529,
            0xF7BD48C1,
            0x09134E17,
            0x8343FECA,
            0x7DB6C6B7,
            0x0BF5DFF5,
            0xD810B499
    };

    /**
     * Creates an instance. The number of counters is 2**bucketPow2bits.
     *
     * @param bucketPow2bits number of bits
     * @param numHashers number of hash rounds to perform;
     */
    public CountMinSketch(final int bucketPow2bits, final int numHashers){

        if(bucketPow2bits > 20){
            throw new IllegalArgumentException(
                    String.format("Number of numHashers \'%d\' cannot exceed %d with current seeds",
                            numHashers, seeds.length));
        }

        if(numHashers > seeds.length -1){
            throw new IllegalArgumentException(
                    String.format("Number of numHashers \'%d\' cannot exceed %d with current seeds",
                            numHashers, seeds.length));
        }

        this.numBuckets = 1 << bucketPow2bits;
        this.bucketMask = numBuckets - 1;

        this.numHashers = numHashers;

        counters = new long[numHashers][numBuckets];

    }

    /**
     * default - for serialization
     */
    private CountMinSketch(){
    }

    private final Hash hash = MurmurHash.getInstance(MurmurHash.MURMUR_HASH);

    // temp variables of incr.
    int h, index;
    long v;

    public void incr(final Text value){

        numInserts++;

        for(int i = 0; i < numHashers; i++){
            h = hash.hash(value.getBytes(), value.getLength(), seeds[i]);
            index = h & bucketMask;
            v = counters[i][index];
            counters[i][index] = v + 1;
        }
    }

    public long count(final Text value){
        long result = Long.MAX_VALUE;
        for(int seedIdx = 0; seedIdx < numHashers; seedIdx++){
            h = hash.hash(value.getBytes(), value.getLength(), seeds[seedIdx]);
            index = h & bucketMask;
            v = counters[seedIdx][index];
            result = Math.min(v,result);
        }
        return result;
    }

    public static CountMinSketch read(DataInput in) throws IOException {
        CountMinSketch cms = new CountMinSketch();
        cms.readFields(in);
        return cms;
    }

    public void readFields(DataInput in) throws IOException {
        numBuckets = in.readInt();
        bucketMask = in.readInt();
        numHashers = in.readInt();

        numInserts = in.readLong();

        counters = new long[numHashers][numBuckets];

        for(int h = 0; h < numHashers; h++){
            for(int b = 0; b < numBuckets; b++){
                counters[h][b] = in.readLong();
            }
        }
    }

    public void write(DataOutput out) throws IOException {
        out.writeInt(numBuckets);
        out.writeInt(bucketMask);
        out.writeInt(numHashers);

        out.writeLong(numInserts);

        for(int h = 0; h < numHashers; h++){
            for(int b = 0; b < numBuckets; b++){
                out.writeLong(counters[h][b]);
            }
        }
    }
}
