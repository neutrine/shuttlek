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

import com.neutrine.shuttlek.common.Schema
import com.neutrine.shuttlek.common.serializer.SerializerType

/**
 * Encapsulates a message to be sent to kafka.
 * @property topic Kafka topic name
 * @property key Message key value
 * @property value Message content
 * @property serializerType Serializer to use when sending the message
 * @property schema Schema to use for message serialization
 *
 * @author Luiz Picanço
 */
data class ProducerMessage(
    val topic: String,
    val key: String? = null,
    val value: Any,
    val serializerType: SerializerType = SerializerType.Json,
    val schema: Schema? = null
)
