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
package org.apache.edcoleman.cuddly_chainsaw.iterators;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.iterators.IteratorEnvironment;
import org.apache.accumulo.core.iterators.SortedKeyValueIterator;
import org.apache.accumulo.core.iterators.WrappingIterator;

import java.io.IOException;
import java.util.Map;

public class RowCounterIterator extends WrappingIterator {

    private boolean done;

    public RowCounterIterator() {}

    public RowCounterIterator(RowCounterIterator rowCounterIterator, IteratorEnvironment env){
        super();
        setSource(rowCounterIterator.getSource().deepCopy(env));
    }

    public void init(SortedKeyValueIterator<Key, Value> source, Map<String,String> options, IteratorEnvironment env) throws IOException {
        super.init(source, options, env);
    }

    @Override
    public void next() throws IOException {
//        if(done){
//            return;
//        }

        Key nextKey = getSource().getTopKey();
        System.out.println("NK:" + nextKey.toString());

        super.next();


    }
}
