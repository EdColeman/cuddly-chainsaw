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

import java.util.Map;
import java.util.TreeMap;

public class TopKwCF {

    private int maxItems = 10;
    private int reportingThreshold = 2;

    private TreeMap<KeyValueCounter.ByCountKey, KeyValueCounter> byCount = new TreeMap<>();
    private TreeMap<Text, KeyValueCounter> byValue = new TreeMap<>();

    private CountMinSketch cms = new CountMinSketch(8, 5);

    public TopKwCF(){

    }

    public KeyValueCounter add(final Text value, final Text cf){

        cms.incr(value);
        long estimate = cms.count(value);

        KeyValueCounter prev = byValue.get(value);

        if(prev != null){

            KeyValueCounter.ByCountKey bc = prev.getByCountKey();
            byCount.remove(bc);

            prev.update(cf);

            byCount.put(prev.getByCountKey(), prev);
            byValue.put(value, prev);

            return null;
        }

        Map.Entry<KeyValueCounter.ByCountKey, KeyValueCounter> min = byCount.firstEntry();

        if(min.getValue().getCount() > estimate && byCount.size() > maxItems){
            byCount.remove(min.getKey());
        }

        return null;
    }

}
