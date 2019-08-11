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

import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;

public class TopK {

    private int maxItems = 10;
    private int reportingThreshold = 2;

    private TreeSet<ValueCounter> topValueCounters = new TreeSet<>();

    public TopK(){

    }

}
