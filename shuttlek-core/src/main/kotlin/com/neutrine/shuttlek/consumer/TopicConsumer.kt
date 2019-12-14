/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.neutrine.shuttlek.consumer

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import java.time.Duration

/**
 * TopicConsumer
 *
 * @param kafkaConsumer The [KafkaConsumer]
 * @param consumerRecordProcessor The [ConsumerRecordProcessor] to use to process the messages
 * @param pollTimeout The maximum time to block between each poll.
 *
 * @author Luiz Picanço
 */
class TopicConsumer(
    private val kafkaConsumer: KafkaConsumer<String, ByteArray>,
    private val consumerRecordProcessor: ConsumerRecordProcessor,
    val pollTimeout: Duration = Duration.ofMinutes(1)
) {
    /**
     * Starts the topic consumer.
     *
     * @param topics List of the topics to subscribe.
     */
    fun start(vararg topics: String) {
        kafkaConsumer.subscribe(topics.toMutableList())

        kafkaConsumer.use {
            while (true) {
                kafkaConsumer.poll(pollTimeout).forEach() {
                    consumerRecordProcessor.process(it)
                    commitMessage(it)
                }
            }
        }
    }

    private fun commitMessage(message: ConsumerRecord<String, ByteArray>) {
        val offsets = mutableMapOf(TopicPartition(message.topic(), message.partition()) to OffsetAndMetadata(message.offset() + 1))
        kafkaConsumer.commitSync(offsets)
    }
}
