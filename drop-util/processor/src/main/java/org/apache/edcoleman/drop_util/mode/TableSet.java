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

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicReference;

public class TableSet {

    private static class DefaultComparator implements Comparator<AtomicReference<TableRecord>> {
        @Override
        public int compare(AtomicReference<TableRecord> r1, AtomicReference<TableRecord> r2) {
            return r1.get().getTableName().compareTo(r2.get().getTableName());
        }
    }

    public class SizeComparator extends DefaultComparator {
        @Override
        public int compare(AtomicReference<TableRecord> r1, AtomicReference<TableRecord> r2) {
            int result = Long.compare(r1.get().getSize(), r2.get().getSize());
            if(result != 0){
                return result;
            }
            return super.compare(r1,r2);
        }
    }

    public static class NumTablesComparator extends DefaultComparator {
        @Override
        public int compare(AtomicReference<TableRecord> r1, AtomicReference<TableRecord> r2) {
            int result = Integer.compare(r1.get().getNumTablets(), r2.get().getNumTablets());

            if(result != 0){
                return result;
            }

            return super.compare(r1,r2);
        }
    }


    private static class ByOldestQueued extends DefaultComparator {

        @Override
        public int compare(AtomicReference<TableRecord> r1, AtomicReference<TableRecord> r2) {

            int result = r1.get().getQueued().compareTo(r2.get().getQueued());

            if(result != 0){
                return result;
            }

            return super.compare(r1,r2);
        }
    }

    private ConcurrentSkipListSet<AtomicReference<TableRecord>> tableSet = new ConcurrentSkipListSet<>(new DefaultComparator());

    public void add(final TableRecord record) {
        tableSet.add(new AtomicReference<>(record));
    }

    public Set<AtomicReference<TableRecord>> OrderedByOldestView() {

        Set<AtomicReference<TableRecord>> tables;

        tables = new TreeSet<>(new ByOldestQueued());
        tables.addAll(tableSet);

        return tables;
    }
}



