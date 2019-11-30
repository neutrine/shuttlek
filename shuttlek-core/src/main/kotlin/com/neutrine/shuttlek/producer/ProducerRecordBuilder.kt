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

import com.neutrine.shuttlek.common.MessageHeaders
import com.neutrine.shuttlek.common.serdes.JsonSerdes
import com.neutrine.shuttlek.common.serializer.SerializerType
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeaders

/**
 * [ProducerRecord] Builder
 *
 * @author Luiz Picanço
 */
internal object ProducerRecordBuilder {
    fun from(producerMessage: ProducerMessage): ProducerRecord<String, ByteArray> {
        val value = serializeValue(producerMessage)
        val headers = RecordHeaders()
            .add(MessageHeaders.SERIALIZER_TYPE, producerMessage.serializerType.code.toByteArray())

        producerMessage.schemaName?.let { headers.add(MessageHeaders.SCHEMA_NAME, it.toByteArray()) }
        producerMessage.schemaVersion?.let { headers.add(MessageHeaders.SCHEMA_VERSION, it.toByteArray()) }

        return ProducerRecord<String, ByteArray>(producerMessage.topic, null, producerMessage.key, value, headers)
    }

    private fun serializeValue(producerMessage: ProducerMessage): ByteArray =
        when (producerMessage.serializerType) {
            SerializerType.Json -> JsonSerdes.serialize(producerMessage.value)
        }
}
