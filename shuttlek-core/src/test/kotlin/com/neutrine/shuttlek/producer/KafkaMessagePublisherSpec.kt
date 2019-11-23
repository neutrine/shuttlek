package com.neutrine.shuttlek.producer

import com.neutrine.shuttlek.common.MessageHeaders
import com.neutrine.shuttlek.common.serializer.SerializerType
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.header.Headers
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.*
import java.util.concurrent.Future
import kotlin.test.assertEquals

object KafkaMessagePublisherSpec: Spek({
    describe("A KafkaMessagePublisher") {
        val kafkaProducerMock by memoized { mockk<KafkaProducer<String, ByteArray>>()}
        val kafkaMessagePublisher by memoized { KafkaMessagePublisher(kafkaProducerMock) }

        describe("publish") {
            val message by memoized { ProducerMessage(
                topic = "topic",
                key = "42",
                value = Date(),
                serializerType = SerializerType.Json,
                schemaName = "Base",
                schemaVersion = "1") }
            val producerRecordSlot by memoized { slot<ProducerRecord<String, ByteArray>>() }

            beforeEachTest {
                every { kafkaProducerMock.send(capture(producerRecordSlot)) } returns mockk(relaxed = true)
                kafkaMessagePublisher.publish(message)
            }

            it("should send message to correct topic") {
                assertEquals(message.topic, producerRecordSlot.captured.topic())
            }

            it("should send message with correct key") {
                assertEquals(message.key, producerRecordSlot.captured.key())
            }

            it("should send message with correct value") {
                val expected = (message.value as Date).time
                assertEquals(expected.toString(), String(producerRecordSlot.captured.value()))
            }

            it("should send serialization type header") {
                assertEquals(message.serializerType.code, getHeaderAsString(producerRecordSlot.captured.headers(), MessageHeaders.SERIALIZER_TYPE))
            }

            it("should send schema name header") {
                assertEquals(message.schemaName, getHeaderAsString(producerRecordSlot.captured.headers(), MessageHeaders.SCHEMA_NAME))
            }

            it("should send schema version header") {
                assertEquals(message.schemaVersion, getHeaderAsString(producerRecordSlot.captured.headers(), MessageHeaders.SCHEMA_VERSION))
            }
        }
        describe("publish async") {
            val message by memoized { ProducerMessage(
                topic = "topic",
                key = "42",
                value = Date(),
                serializerType = SerializerType.Json,
                schemaName = "Base",
                schemaVersion = "1") }

            it("should send message async") {
                val futureMock = mockk<Future<RecordMetadata>>()
                every { kafkaProducerMock.send(any()) } returns futureMock
                assertEquals(futureMock, kafkaMessagePublisher.publishAsync(message))
            }

        }
    }
})

fun getHeaderAsString(headers: Headers, key: String): String = String(headers.lastHeader(key).value())
