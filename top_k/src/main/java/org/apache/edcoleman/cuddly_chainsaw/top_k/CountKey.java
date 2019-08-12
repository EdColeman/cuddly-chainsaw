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
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.StringJoiner;

class CountKey implements Writable, Comparable<CountKey>{

    private Text value;
    private long count;

    public CountKey(Text value, long count){
        this.value = value;
        this.count = count;
    }

    CountKey(){
        value = new Text();
        count = 0;
    }

    public Text getValue() {
        return value;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long value) {
        count = value;
    }

    /**
     * Compare this CountKey with the specified value. The sort by descending count and then value.
     *
     * @param other value to compare.
     *
     * @return -1, 0 or 1 order by descending count and then value.
     */
    @Override
    public int compareTo(CountKey other) {
        int r = Long.compare(this.count, other.count);
        if(r != 0){
            return r;
        }

        return this.value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CountKey.class.getSimpleName() + "[", "]")
                .add("value=" + value)
                .add("count=" + count)
                .toString();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        value.write(out);
        out.writeLong(count);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        value.readFields(in);
        count = in.readLong();
    }

    public static CountKey read(DataInput in) throws IOException {
        CountKey k = new CountKey();
        k.readFields(in);
        return  k;
    }
}
