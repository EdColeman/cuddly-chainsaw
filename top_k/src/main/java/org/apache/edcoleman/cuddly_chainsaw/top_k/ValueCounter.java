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

import org.apache.accumulo.core.data.Key;
import org.apache.hadoop.io.Text;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ValueCounter {

    long count = 0;
    Map<Text,Long> colFamCounts = new HashMap<>();

    // private final Text noCF = new Text("");

    public void update(final Key key){
        count++;

        Text cf = key.getColumnFamily();

        Long cfCount = colFamCounts.get(cf);

        if(cfCount == null){
            colFamCounts.put(cf, 1L);
        } else {
            colFamCounts.put(cf, ++cfCount);
        }
    }

    public long getTotalCount() {
        return count;
    }

    public int getNumCFs(){
        return colFamCounts.size();
    }

    public Set<Text> getCfNames(){
        return colFamCounts.keySet();
    }

    public long getCfCount(final Text cfName){
        Long v = colFamCounts.get(cfName);
        if(v == null){
            return -1;
        }

        return v;
    }

    public Set<Map.Entry<Text,Long>> getCfCounts(){
        return Collections.unmodifiableSet(colFamCounts.entrySet());
    }

}
