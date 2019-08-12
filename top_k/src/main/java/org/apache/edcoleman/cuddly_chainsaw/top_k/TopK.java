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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

public class TopK {

    public static final int DEFAULT_MAX_ITEMS = 10;
    public static final int CMS_POW2_BITS = 12;
    public static final int CMS_NUM_HASHES = 5;

    private final int maxItems;
    private final CountMinSketch cms;

    private int reportingThreshold = 2;


    private TreeSet<CountKey> byCount = new TreeSet<>();
    private Map<Text, CountKey> byValue = new TreeMap<>();

    public TopK(){
        this(DEFAULT_MAX_ITEMS, new CountMinSketch(CMS_POW2_BITS, CMS_NUM_HASHES));
    }

    public TopK(final int maxItems, final CountMinSketch cms){
        this.maxItems = maxItems;
        this.cms = cms;
    }

    public KeyValueCounter add(final Text value){

        cms.incr(value);
        long estimate = cms.count(value);

        CountKey current = byValue.get(value);

        if(current != null){

            byCount.remove(current);
            current.setCount(estimate);

            updateState(value, current);

            return null;
        }

        if(byCount.size() <= 0){
            CountKey update = new CountKey(value, estimate);
            updateState(value, update);
            return null;
        }

        CountKey min = byCount.first();

        if(min.getCount() > estimate && byCount.size() >= maxItems){

            // eject smallest
            byCount.remove(min);
            byValue.remove(min.getValue());

            CountKey update = new CountKey(value, estimate);
            updateState(value, update);

        } else if(byCount.size() < maxItems ){
            CountKey update = new CountKey(value, estimate);
            updateState(value, update);
        }

        return null;
    }

    private void updateState(Text value, CountKey update) {
        byCount.add(update);
        byValue.put(value, update);
    }

    public Set<CountKey> getTopK(){
        return Collections.unmodifiableSet(byCount.descendingSet());
    }


    public static TopK read(DataInput in) throws IOException {

        TreeSet<CountKey> byCount = new TreeSet<>();
        Map<Text, CountKey> byValue = new TreeMap<>();

        int maxItems = in.readInt();
        int numItems = in.readInt();

        for(int i = 0; i < numItems; i++){
            CountKey k = CountKey.read(in);
            byCount.add(k);
            byValue.put(k.getValue(),k);
        }
        CountMinSketch cms = CountMinSketch.read(in);

        TopK topK = new TopK(maxItems, cms);

        return topK;
    }

    public void readFields(DataInput in) throws IOException {

    }

    public void write(DataOutput out) throws IOException {

    }

}
