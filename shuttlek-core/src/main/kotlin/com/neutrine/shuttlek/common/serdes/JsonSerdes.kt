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

package com.neutrine.shuttlek.common.serdes

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.reflect.KClass

/**
 * Json serializer object
 *
 * @author Luiz Picanço
 */
internal object JsonSerdes : Serdes {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
        .registerModule(
            JavaTimeModule()
                .addSerializer(
                    LocalTime::class.java,
                    object : JsonSerializer<LocalTime>() {
                        override fun serialize(value: LocalTime, gen: JsonGenerator, serializers: SerializerProvider) {
                            gen.writeNumber(ChronoUnit.MILLIS.between(LocalTime.MIDNIGHT, value))
                        }
                    }
                ).addDeserializer(
                    LocalTime::class.java,
                    object : JsonDeserializer<LocalTime>() {
                        override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): LocalTime =
                            LocalTime.MIDNIGHT.plus(p.longValue, ChronoUnit.MILLIS)
                    }
                ).addSerializer(
                    LocalDate::class.java,
                    object : JsonSerializer<LocalDate>() {
                        override fun serialize(value: LocalDate, gen: JsonGenerator, serializers: SerializerProvider) {
                            gen.writeNumber(value.toEpochDay())
                        }
                    }
                ).addSerializer(
                    LocalDateTime::class.java,
                    object : JsonSerializer<LocalDateTime>() {
                        override fun serialize(value: LocalDateTime, gen: JsonGenerator, serializers: SerializerProvider) {
                            gen.writeNumber(ChronoUnit.MILLIS.between(LocalDateTime.of(LocalDate.ofEpochDay(0), LocalTime.MIDNIGHT), value))
                        }
                    }
                ).addDeserializer(
                    LocalDateTime::class.java,
                    object : JsonDeserializer<LocalDateTime>() {
                        override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): LocalDateTime =
                            LocalDateTime.of(LocalDate.ofEpochDay(0), LocalTime.MIDNIGHT).plus(p.longValue, ChronoUnit.MILLIS)
                    }
                )
        ).enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)

    /**
     * Returns the json representation of the given [value].
     */
    override fun serialize(value: Any): ByteArray {
        return objectMapper.writeValueAsBytes(value)
    }

    /**
     * Deserialize the the given [value] in json
     * to [deserializeToType].
     */
    override fun <T : Any> deserialize(value: ByteArray, deserializeToType: KClass<T>): T {
        return objectMapper.readValue(value, deserializeToType.java)
    }
}
