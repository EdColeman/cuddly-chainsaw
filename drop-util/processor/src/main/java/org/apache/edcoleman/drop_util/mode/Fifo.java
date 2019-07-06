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
package org.apache.edcoleman.drop_util.mode;

import org.apache.edcoleman.drop_util.message.TableRecord;

import java.time.Instant;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Fifo implements Selector {

    private final Instant created = Instant.now();

    private final Iterator<AtomicReference<TableRecord>> cursor;
    private final Set<AtomicReference<TableRecord>> tableView;

    public Fifo(final Set<AtomicReference<TableRecord>> candidates){
        tableView = candidates;
        cursor = tableView.iterator();
    }

    @Override
    public boolean hasNext() {
        return cursor.hasNext();
    }


    @Override
    public TableRecord next() {
        return cursor.next().get();
    }

    @Override
    public void remove() {
       throw new UnsupportedOperationException("remove not allowed");
    }

    @Override
    public void forEachRemaining(Consumer<? super TableRecord> action) {

    }


}
