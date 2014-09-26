/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 *  Kafka Spark Consumer code is taken from Kafka spout of the Apache Storm project (https://github.com/apache/storm/tree/master/external/storm-kafka), 
 *  which was originally created by wurstmeister (https://github.com/wurstmeister/storm-kafka-0.8-plus)
 *  This file has been modified to work with Spark Streaming.
 */

package consumer.kafka;

import java.io.Serializable;

public interface BrokerHosts extends Serializable {

}