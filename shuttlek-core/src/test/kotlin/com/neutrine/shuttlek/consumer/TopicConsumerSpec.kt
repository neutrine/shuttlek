package com.neutrine.shuttlek.consumer

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

internal object TopicConsumerSpec : Spek({
    describe("A TopicConsumer") {
        val kafkaConsumerMock by memoized { mockk<KafkaConsumer<String, ByteArray>>(relaxUnitFun = true) }
        val consumerRecordProcessorMock by memoized { mockk<ConsumerRecordProcessor>(relaxUnitFun = true) }
        val topicConsumer by memoized { TopicConsumer(kafkaConsumerMock, consumerRecordProcessorMock) }

        describe("start") {
            val topic = "the_topic"
            val consumerRecord by memoized { ConsumerRecord<String, ByteArray>("topic", 3, 10, "key", "value".toByteArray()) }
            val topicPartitionOffsetSlot by memoized { slot<MutableMap<TopicPartition, OffsetAndMetadata>>() }

            beforeEachTest {
                coEvery { kafkaConsumerMock.poll(topicConsumer.pollTimeout) } coAnswers {
                    delay(30)
                    ConsumerRecords(mutableMapOf(TopicPartition("topic", 1) to listOf(consumerRecord)))
                }

                GlobalScope.launch {
                    topicConsumer.start(topic)
                }
            }

            it("should call the consumerRecordProcessor") {
                verify(timeout = 10000L) { consumerRecordProcessorMock.process(consumerRecord) }
            }

            it("should commit the message after the processor") {
                verify(timeout = 10000) { kafkaConsumerMock.commitSync(capture(topicPartitionOffsetSlot)) }

                with(topicPartitionOffsetSlot.captured.keys.first()) {
                    assertEquals(consumerRecord.topic(), topic())
                    assertEquals(consumerRecord.partition(), partition())
                }
                with(topicPartitionOffsetSlot.captured.values.first()) {
                    assertEquals(consumerRecord.offset() + 1, offset())
                }
            }
        }
    }
})
