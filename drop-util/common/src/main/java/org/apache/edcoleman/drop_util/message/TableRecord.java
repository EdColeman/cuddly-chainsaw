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
package org.apache.edcoleman.drop_util.message;


import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;
import java.util.StringJoiner;

public class TableRecord implements Comparable<TableRecord> {

    public static class Builder {

        private String tableName;
        private Instant timestamp;
        private Instant queued;
        private DropStates state;
        private long size = -1;
        private int numTablets = -1;

        public Builder(final String tableName){
            this.tableName = tableName;
            this.timestamp = Instant.now();
            this.queued = timestamp;
            this.state = DropStates.UNKNOWN;
        }

        public Builder(final TableRecord other){
            this.tableName = other.tableName;
            this.timestamp = Instant.now();
            this.queued = other.queued;
            this.state = other.state;
            this.numTablets = other.numTablets;
        }

        public Builder queuedAt(final Instant timestamp){
            this.queued = timestamp;
            return this;
        }

        public Builder withState(final DropStates state){
            this.state = state;
            return this;
        }

        public Builder withSize(final long size){
            this.size = size;
            return this;
        }

        public Builder withNumTablets(final int numTablets){
            this.numTablets = numTablets;
            return this;
        }

        public TableRecord build(){
            return new TableRecord(tableName, timestamp, queued, state, size, numTablets);
        }
    }

    private final String tableName;
    private final Instant timestamp;
    private final Instant queued;
    private final DropStates state;
    private final long size;
    private final int numTablets;

    private TableRecord(final String tableName, final Instant timestamp, final Instant queued, final DropStates state, final long size, final int numTablets){
        this.tableName = tableName;
        this.timestamp = timestamp;
        this.queued = queued;
        this.state = state;
        this.size = size;
        this.numTablets = numTablets;
    }

    public String getTableName() {
        return tableName;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Instant getQueued() {
        return queued;
    }

    public DropStates getState() {
        return state;
    }

    public long getSize() {
        return size;
    }

    public int getNumTablets() {
        return numTablets;
    }

    public static final Comparator<TableRecord> COMPARATOR =
            Comparator.comparing(TableRecord::getTableName)
            .thenComparing(TableRecord::getQueued)
                .thenComparing(TableRecord::getState);

    /**
     * Sort by table name, queued timestamp and state to generate hashcode - consistent with equals.
     * @param other a record to compare
     * @return 0 if matches.
     */
    @Override
    public int compareTo(TableRecord other) {
        return COMPARATOR.compare(this, other);
    }

    /**
     * Uses tablename, queued timestamp and state to determine if two TableRecords are equal.
     * @param other to compare
     * @return true if other is equal.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        TableRecord that = (TableRecord) other;
        return tableName.equals(that.tableName) &&
                Objects.equals(queued, that.queued) &&
                state == that.state;
    }

    /**
     * Uses table name, queued timestamp and state to generate hashcode - consistent with equals.
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(tableName, queued, state);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TableRecord.class.getSimpleName() + "[", "]")
                .add("tableName='" + tableName + "'")
                .add("timestamp=" + timestamp)
                .add("queued=" + queued)
                .add("state=" + state)
                .add("size=" + size)
                .add("numTablets=" + numTablets)
                .toString();
    }
}
