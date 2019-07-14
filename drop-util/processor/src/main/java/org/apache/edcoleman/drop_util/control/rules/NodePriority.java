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
package org.apache.edcoleman.drop_util.control.rules;

import java.util.Comparator;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Provides node priority ordering, 1 if highest (first), 99 is lowest (last). Equals and hashCode only consider
 * priority, the description is supplemental informational.
 */
public class NodePriority implements Comparable<NodePriority> {

    public static final int HIGHEST = 1;
    public static final int LOWEST = 99;

    private final int priority;
    private final String description;

    /**
     * Create an immutable instance of NodePriority. Throws IllegalArgumentException is priority is not
     * between 1 (highest priority) and 99 (lowest priority).
     *
     * @param priority the node priority
     * @param description supplemental description
     */
    public NodePriority(final int priority, final String description){
        if(priority < HIGHEST || priority > LOWEST){
            throw new IllegalArgumentException(String.format("Invalid priority, received %d, priority must be between %d and %d",
                    priority, HIGHEST, LOWEST));
        }
        this.priority = priority;
        this.description = description;
    }

    @Override
    public int compareTo(NodePriority other) {
        return Integer.compare(this.priority, other.priority);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodePriority that = (NodePriority) o;
        return priority == that.priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(priority);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NodePriority.class.getSimpleName() + "[", "]")
                .add("priority=" + priority)
                .add("description='" + description + "'")
                .toString();
    }


}
