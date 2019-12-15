/*
 * Copyright 2019, the original author or authors.
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

import com.neutrine.shuttlek.common.MessageHeaders
import com.neutrine.shuttlek.common.serializer.SerializerType
import com.neutrine.shuttlek.common.valueAsString
import org.apache.kafka.clients.consumer.ConsumerRecord

/**
 * Processor for ConsumerRecord.
 *
 * @author Luiz Picanço
 */
class ConsumerRecordProcessor(
    private val messageHandler: MessageHandler,
    private val consumerRecordFilter: ConsumerRecordFilter = ConsumerRecordFilter()
) {
    fun process(record: ConsumerRecord<String, ByteArray>) {
        val headers = record.headers()
        val schemaName = headers.valueAsString(MessageHeaders.SCHEMA_NAME)

        if (!consumerRecordFilter.filter(schemaName)) {
            return
        }

        val schemaVersion = headers.valueAsString(MessageHeaders.SCHEMA_VERSION)
        val serializer = headers.valueAsString(MessageHeaders.SERIALIZER_TYPE)
        val consumerMessage = ConsumerMessage(
            partition = record.partition(),
            key = record.key(),
            value = record.value(),
            schemaName = schemaName,
            schemaVersion = schemaVersion,
            serializerType = serializer?.let { SerializerType.parseCode(serializer) })

        messageHandler.handle(consumerMessage)
    }
}
