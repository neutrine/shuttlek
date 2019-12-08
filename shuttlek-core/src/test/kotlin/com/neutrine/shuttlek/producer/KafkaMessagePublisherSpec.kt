package com.neutrine.shuttlek.producer

import com.neutrine.shuttlek.common.MessageHeaders
import com.neutrine.shuttlek.common.Schema
import com.neutrine.shuttlek.common.serializer.SerializerType
import com.neutrine.shuttlek.common.valueAsString
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.Date
import java.util.concurrent.Future
import kotlin.test.assertEquals

object KafkaMessagePublisherSpec : Spek({
    describe("A KafkaMessagePublisher") {
        val kafkaProducerMock by memoized { mockk<KafkaProducer<String, ByteArray>>() }
        val kafkaMessagePublisher by memoized { KafkaMessagePublisher(kafkaProducerMock) }

        describe("publish") {
            val message by memoized { ProducerMessage(
                topic = "topic",
                key = "42",
                value = Date(),
                serializerType = SerializerType.Json,
                schema = Schema("Base", "1")) }
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
                assertEquals(message.serializerType.code, producerRecordSlot.captured.headers().valueAsString(MessageHeaders.SERIALIZER_TYPE))
            }

            it("should send schema name header") {
                assertEquals(message.schema?.name, producerRecordSlot.captured.headers().valueAsString(MessageHeaders.SCHEMA_NAME))
            }

            it("should send schema version header") {
                assertEquals(message.schema?.version, producerRecordSlot.captured.headers().valueAsString(MessageHeaders.SCHEMA_VERSION))
            }
        }
        describe("publish async") {
            val message by memoized { ProducerMessage(
                topic = "topic",
                key = "42",
                value = Date(),
                serializerType = SerializerType.Json,
                schema = Schema("Base", "1")) }

            it("should send message async") {
                val futureMock = mockk<Future<RecordMetadata>>()
                every { kafkaProducerMock.send(any()) } returns futureMock
                assertEquals(futureMock, kafkaMessagePublisher.publishAsync(message))
            }
        }
    }
})
