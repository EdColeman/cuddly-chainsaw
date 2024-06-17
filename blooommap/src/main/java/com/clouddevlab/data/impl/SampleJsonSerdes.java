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

import com.clouddevlab.data.Sample;
import com.google.gson.Gson;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SampleJsonSerdes implements SampleSerdes {

  private final Gson gson = new Gson();

  @Override
  public void write(final DataOutput out, final SampleValue value) {
    try {
      BytesWritable bytes = new BytesWritable(gson.toJson(value).getBytes(UTF_8));
      bytes.write(out);
    } catch (IOException ex) {
      throw new IllegalStateException("failed to write sample value: " + value, ex);
    }
  }

  @Override
  public SampleValue read(final DataInput in) {

    return null;
  }
}
