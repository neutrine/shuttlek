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

package com.neutrine.shuttlek.producer

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import java.util.concurrent.Future

/**
 *  A kafka message publisher for the given [kafkaProducer].
 *
 *  @author Luiz Picanço
 */
class KafkaMessagePublisher(private val kafkaProducer: KafkaProducer<String, ByteArray>) {

    /**
     * Publish the given [producerMessage] to kafka
     */
    fun publish(producerMessage: ProducerMessage) {
        publishAsync(producerMessage).get()
    }

    /**
     * Publish the given [producerMessage] to kafka asynchronous and return a [Future<RecordMetada>]
     */
    fun publishAsync(producerMessage: ProducerMessage): Future<RecordMetadata> {
        val producerRecord = ProducerRecordBuilder.from(producerMessage)
        return kafkaProducer.send(producerRecord)
    }
}