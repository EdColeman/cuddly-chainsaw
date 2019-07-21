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

import java.util.Collection;
import java.util.function.Function;

public class AI {


    public Node.State x(){
        return Node.State.INIT;
    }

//    @FunctionalInterface
//    public interface Selector {
//        String method(Collection<Node> nodes);
//    }

    public class Selector implements Function<Collection<Node>, Node.State> {
        @Override
        public Node.State apply(Collection<Node> nodes){
            for(Node node: nodes){
                Node.State childState = node.tick();
                if(childState == Node.State.RUNNING){
                    return Node.State.RUNNING;
                }

                if(childState == Node.State.SUCCESS){
                    return Node.State.SUCCESS;
                }
            }

            return Node.State.FAIL;
        }
    }

    public class Sequence implements Function<Collection<Node>, Node.State> {
        @Override
        public Node.State apply(Collection<Node> nodes){
            for(Node node: nodes){
                Node.State childState = node.tick();
                if(childState == Node.State.RUNNING){
                    return Node.State.RUNNING;
                }

                if(childState == Node.State.FAIL){
                    return Node.State.FAIL;
                }
            }

            return Node.State.SUCCESS;
        }
    }


}
