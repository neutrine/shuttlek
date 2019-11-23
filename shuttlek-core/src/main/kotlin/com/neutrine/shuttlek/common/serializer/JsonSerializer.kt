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

package com.neutrine.shuttlek.common.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.*
import java.time.temporal.ChronoUnit

/**
 * Json serializer object
 *
 * @author Luiz Picanço
 */
internal object JsonSerializer {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule()
            .addSerializer(OffsetTime::class.java, object : JsonSerializer<OffsetTime>() {
                override fun serialize(value: OffsetTime, gen: JsonGenerator, serializers: SerializerProvider) {
                    gen.writeNumber(ChronoUnit.MILLIS.between(LocalTime.MIDNIGHT, value.toLocalTime().plusSeconds(value.offset.totalSeconds.toLong())))
                }
            }).addSerializer(LocalTime::class.java, object : JsonSerializer<LocalTime>() {
                override fun serialize(value: LocalTime, gen: JsonGenerator, serializers: SerializerProvider) {
                    gen.writeNumber(ChronoUnit.MILLIS.between(LocalTime.MIDNIGHT, value))
                }
            }).addSerializer(LocalDate::class.java, object : JsonSerializer<LocalDate>() {
                override fun serialize(value: LocalDate, gen: JsonGenerator, serializers: SerializerProvider) {
                    gen.writeNumber(value.toEpochDay())
                }
            }).addSerializer(LocalDateTime::class.java, object : JsonSerializer<LocalDateTime>() {
                override fun serialize(value: LocalDateTime, gen: JsonGenerator, serializers: SerializerProvider) {
                    gen.writeNumber(ChronoUnit.MILLIS.between(LocalDateTime.of(LocalDate.ofEpochDay(0), LocalTime.MIDNIGHT), value))
                }
            })
        ).enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)

    /**
     * Returns the json represention of the given [value].
     */
    fun serialize(value: Any): ByteArray {
        return objectMapper.writeValueAsBytes(value)
    }
}
