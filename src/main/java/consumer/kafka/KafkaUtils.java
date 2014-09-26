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

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.TopicAndPartition;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.OffsetRequest;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaUtils {

	public static final Logger LOG = LoggerFactory.getLogger(KafkaUtils.class);
	private static final int NO_OFFSET = -5;

	public static long getOffset(SimpleConsumer consumer, String topic,
			int partition, KafkaConfig config) {
		long startOffsetTime = kafka.api.OffsetRequest.EarliestTime();
		if (config._forceFromStart) {
			startOffsetTime = config._startOffsetTime;
		}
		return getOffset(consumer, topic, partition, startOffsetTime);
	}

	public static long getOffset(SimpleConsumer consumer, String topic,
			int partition, long startOffsetTime) {
		TopicAndPartition topicAndPartition = new TopicAndPartition(topic,
				partition);
		Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
		requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(
				startOffsetTime, 1));
		OffsetRequest request = new OffsetRequest(requestInfo,
				kafka.api.OffsetRequest.CurrentVersion(), consumer.clientId());

		long[] offsets = consumer.getOffsetsBefore(request).offsets(topic,
				partition);
		if (offsets.length > 0) {
			return offsets[0];
		} else {
			return NO_OFFSET;
		}
	}

	public static FetchResponse fetchMessages(KafkaConfig config,
			SimpleConsumer consumer, Partition partition, long offset) {
		ByteBufferMessageSet msgs = null;
		String topic = (String) config._stateConf.get(Config.KAFKA_TOPIC);
		int partitionId = partition.partition;

		// for (int errors = 0; errors < 2 && msgs == null; errors++) {
		FetchRequestBuilder builder = new FetchRequestBuilder();
		FetchRequest fetchRequest = builder
				.addFetch(topic, partitionId, offset, config._fetchSizeBytes)
				.clientId(
						(String) config._stateConf
								.get(Config.KAFKA_CONSUMER_ID)).build();
		FetchResponse fetchResponse;
		try {
			fetchResponse = consumer.fetch(fetchRequest);
		} catch (Exception e) {
			if (e instanceof ConnectException) {
				throw new FailedFetchException(e);
			} else {
				throw new RuntimeException(e);
			}
		}

		return fetchResponse;
	}
}