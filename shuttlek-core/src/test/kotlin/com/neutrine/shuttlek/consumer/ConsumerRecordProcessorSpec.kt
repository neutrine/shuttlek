package com.neutrine.shuttlek.consumer

import com.neutrine.shuttlek.common.MessageHeaders
import com.neutrine.shuttlek.common.serializer.SerializerType
import io.mockk.Called
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal object ConsumerRecordProcessorSpec : Spek({
    describe("A ConsumerRecordProcessor") {
        val messageHandlerMock by memoized { mockk<MessageHandler>() }
        val consumerRecordFilterMock by memoized { mockk<ConsumerRecordFilter>() }
        val consumerRecordProcessor by memoized { ConsumerRecordProcessor(messageHandlerMock, consumerRecordFilterMock) }

        describe("process without headers and filter returning true") {
            val consumerRecord by memoized { ConsumerRecord<String, ByteArray>("topic", 1, 1, "key", "value".toByteArray()) }
            val consumerMessageSlot by memoized { slot<ConsumerMessage>() }

            beforeEachTest {
                every { messageHandlerMock.handle(capture(consumerMessageSlot)) } just Runs
                every { consumerRecordFilterMock.filter(null) } returns true
                consumerRecordProcessor.process(consumerRecord)
            }

            it("should call the messageHandler with correct key") {
                assertEquals(consumerRecord.key(), consumerMessageSlot.captured.key)
            }

            it("should call the messageHandler with correct value") {
                assertEquals(consumerRecord.value(), consumerMessageSlot.captured.value)
            }

            it("should call the messageHandler with correct partition") {
                assertEquals(consumerRecord.partition(), consumerMessageSlot.captured.partition)
            }

            it("should call the messageHandler without schemaName") {
                assertNull(consumerMessageSlot.captured.schemaName)
            }

            it("should call the messageHandler without schemaVersion") {
                assertNull(consumerMessageSlot.captured.schemaVersion)
            }

            it("should call the messageHandler without serializerType") {
                assertNull(consumerMessageSlot.captured.serializerType)
            }
        }
        describe("process without headers and filter returning false") {

            beforeEachTest {
                every { consumerRecordFilterMock.filter(null) } returns false
                consumerRecordProcessor.process(ConsumerRecord("topic", 1, 1, "key", "value".toByteArray()))
            }

            it("should not call the messageHandler") {
                verify { messageHandlerMock wasNot Called }
            }
        }
        describe("process with headers and filter returning true") {
            val serializerType = SerializerType.Json
            val schemaName = "testSchema"
            val schemaVersion = "v1"

            val consumerRecord by memoized { ConsumerRecord<String, ByteArray>("topic", 1, 1, "key", "value".toByteArray())
                .apply { headers().add(MessageHeaders.SERIALIZER_TYPE, serializerType.code.toByteArray())
                    .add(MessageHeaders.SCHEMA_NAME, schemaName.toByteArray())
                    .add(MessageHeaders.SCHEMA_VERSION, schemaVersion.toByteArray()) } }

            val consumerMessageSlot by memoized { slot<ConsumerMessage>() }

            beforeEachTest {
                every { messageHandlerMock.handle(capture(consumerMessageSlot)) } just Runs
                every { consumerRecordFilterMock.filter(schemaName) } returns true
                consumerRecordProcessor.process(consumerRecord)
            }

            it("should call the messageHandler with correct key") {
                assertEquals(consumerRecord.key(), consumerMessageSlot.captured.key)
            }

            it("should call the messageHandler with correct value") {
                assertEquals(consumerRecord.value(), consumerMessageSlot.captured.value)
            }

            it("should call the messageHandler with correct partition") {
                assertEquals(consumerRecord.partition(), consumerMessageSlot.captured.partition)
            }

            it("should call the messageHandler with correct schemaName") {
                assertEquals(schemaName, consumerMessageSlot.captured.schemaName)
            }

            it("should call the messageHandler with correct schemaVersion") {
                assertEquals(schemaVersion, consumerMessageSlot.captured.schemaVersion)
            }

            it("should call the messageHandler with correct serializerType") {
                assertEquals(serializerType, consumerMessageSlot.captured.serializerType)
            }
        }
    }
})
