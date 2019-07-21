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

import org.apache.edcoleman.drop_util.control.Blackboard;

import java.util.*;

public class Root {

    private final Set<Node> nodes = new TreeSet<>(new PriorityComparator());

    private final Blackboard blackboard;

    public Root(final Blackboard blackboard){
        this.blackboard = blackboard;
    }

    public boolean apply(){

        // assuming sequence - success on all children, fail or running on one child.
        for (Node node: nodes) {
             if(!node.tick()){
                 return false;
             }
        }

        return true;
    }

    public void addNode(Node node){
        nodes.add(node);
    }

    public static class PriorityComparator implements Comparator<Node> {

        @Override
        public int compare(Node p1, Node p2) {
            if(p2 == null){
                return 1;
            }
            return p1.getPriority().compareTo(p2.getPriority());
        }

    }
}

