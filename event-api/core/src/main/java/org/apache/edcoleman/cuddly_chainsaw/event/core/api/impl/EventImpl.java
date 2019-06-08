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
package org.apache.edcoleman.cuddly_chainsaw.event.core.api.impl;

import org.apache.edcoleman.cuddly_chainsaw.event.core.api.Event;
import org.apache.edcoleman.cuddly_chainsaw.event.core.api.Id;

import java.util.Map;
import java.util.Optional;

public class EventImpl implements Event {

  private Id traceId;      // required
  private String type;         // required
  private Id id1;          // required
  private Id id2;          // optional
  private long sequenceNumber; // optional
  private long timestamp;      // required

  private Map<String,String> annotations;  // optional

  @Override public Id getTraceId() {
    return null;
  }

  @Override public String getType() {
    return null;
  }

  @Override public Id getId1() {
    return null;
  }

  @Override public Optional<Id> getId2() {
    return Optional.empty();
  }

  @Override public Optional<Long> getSequenceNumber() {
    return Optional.empty();
  }

  @Override public Optional<Long> getTimestamp() {
    return Optional.empty();
  }

  @Override public Map<String,String> getAnnotations() {
    return null;
  }
}
