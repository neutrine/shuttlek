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

package com.neutrine.shuttlek.common

/**
 * Kafka message header constants
 *
 * @author Luiz Picanço
 */
internal object MessageHeaders {
    /**
     * The header containing the serializer used to encode the message.
     */
    const val SERIALIZER_TYPE = "shuttle_serializer_type"
    /**
     * The header containing the schema name of the message.
     */
    const val SCHEMA_NAME = "shuttlek_schema_name"
    /**
     * The header containing the schema version of the message.
     */
    const val SCHEMA_VERSION = "shuttlek_schema_version"
}
