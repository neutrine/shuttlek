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

import com.neutrine.shuttlek.common.serdes.JsonSerdes
import com.neutrine.shuttlek.common.serializer.SerializerType
import kotlin.reflect.KClass

class ConsumerMessage(
    val partition: Int,
    val key: String,
    val value: ByteArray,
    val schemaName: String?,
    val schemaVersion: String?,
    val serializerType: SerializerType?
) {
    /**
     * Deserialize the [value] as a [deserializeToType] type
     * @param deserializeToType Type to deserialize the [value]
     * @return [value] deserialized as [deserializeToType]
     */
    fun <T : Any> valueAs(deserializeToType: KClass<T>): T {
        return JsonSerdes.deserialize(value, deserializeToType)
    }
}
