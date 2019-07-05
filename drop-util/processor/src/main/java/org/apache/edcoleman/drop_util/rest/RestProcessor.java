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
package org.apache.edcoleman.drop_util.rest;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import static spark.Spark.*;

public class RestProcessor {

    private static Logger log = LogManager.getLogger(RestProcessor.class);

    public RestProcessor(){

        log.trace("Hello...");

        port(8081);

        path("/api", () -> {

            before("/*", (q, a) -> log.info("Received api call q:{}, a:{}", q.attributes(), a));

            get("/hello", (req, res) -> "Hello, and now....");

            path("/config", () -> {
                get("/all", (req, res) -> "config....");
                get("/one", (req, res) -> {
                    String id = req.queryParams("p1");
                    return  "p1 = config...." + id;  } );
                put("/set", (req, res) -> {
                    log.info("received put with {}", req.queryParams());
                    return "config...." + req.queryParams("v1");
                });
            });
            path("/metrics", () -> {
                get("/all", (req, res) -> "metrics....");
            });
        } );

//        try {
//            Thread.currentThread().join();
//        }catch(Exception ex){
//            ex.printStackTrace();
//        }

    }

    public void shutdown(){
        spark.Spark.stop();
    }
}
