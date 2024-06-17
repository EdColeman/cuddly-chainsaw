/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.clouddevlab.data.impl;

import com.clouddevlab.data.SampleWriter;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SampleImpl implements SampleWriter {

  private Key key = new Key();
  private Value value = new Value();

  @Override
  public void setName(String name) {
    key.name = name;
  }

  @Override
  public String getName() {
    return key.name;
  }

  @Override
  public String getProp1() {
    return value.prop1;
  }

  /**
   * Wrapper for Sample name that is used as a key in a Map.
   */
  private static class Key implements WritableComparable<Key> {

    private String name;

    @Override
    public int compareTo(Key key) {
      return 0;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {

    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {

    }
  }

  /**
   * Wrapper for Sample properties that are to be stored in a Map.
   */
  private static class Value implements Writable {

    private String prop1;

    @Override
    public void write(DataOutput dataOutput) throws IOException {

    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {

    }
  }

}
