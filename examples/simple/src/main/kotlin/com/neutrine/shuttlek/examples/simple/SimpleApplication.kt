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

package com.neutrine.shuttlek.examples.simple

import com.neutrine.shuttlek.consumer.ConsumerMessage
import com.neutrine.shuttlek.consumer.ConsumerRecordProcessor
import com.neutrine.shuttlek.consumer.MessageHandler
import com.neutrine.shuttlek.consumer.TopicConsumer
import com.neutrine.shuttlek.producer.KafkaMessagePublisher
import com.neutrine.shuttlek.producer.ProducerMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer

/**
 * @author Luiz Picanço
 */
class SimpleApplication {

    fun run() {
        val config = createConfig()
        val producer = KafkaMessagePublisher(KafkaProducer(config))

        val topicConsumer = createTopicConsumer(config)
        GlobalScope.launch { startProducer(producer) }
        topicConsumer.start(TOPIC_NAME)
    }

    private suspend fun startProducer(producer: KafkaMessagePublisher) {
        while(true) {
            val person = Person(System.currentTimeMillis(), "Douglas")
            val message = ProducerMessage(TOPIC_NAME, person.id.toString(), person)
            println("Producing person: ${person.id}")
            producer.publish(message)
            delay(1000)
        }

    }

    private fun createConfig(): Map<String, Any> {
        return mutableMapOf(
            CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG to BOOTSTRAP_SERVERS,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to ByteArraySerializer::class.java,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ByteArrayDeserializer::class.java,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to "false",
            ConsumerConfig.GROUP_ID_CONFIG to "shuttlek-simple-application"
        )
    }

    private fun createConsumer(config: Map<String, Any>) = KafkaConsumer<String, ByteArray>(config)

    private fun createTopicConsumer(config: Map<String, Any>): TopicConsumer {
        val consumer = createConsumer(config)
        val consumerRecordProcessor = ConsumerRecordProcessor(MessageHandlerImpl)
        return TopicConsumer(consumer, consumerRecordProcessor)
    }

    companion object {
        const val BOOTSTRAP_SERVERS = "localhost:9092"
        const val TOPIC_NAME = "simple"
    }
}

object MessageHandlerImpl: MessageHandler {
    override fun handle(consumerMessage: ConsumerMessage) {
        with(consumerMessage) {
            println("Consuming person(partition=$partition, key='$key', value=${String(value)}, schemaName=$schemaName, schemaVersion=$schemaVersion, serializerType=$serializerType)")
            println("Value deserialized(${consumerMessage.valueAs(Person::class)})")

        }
    }
}

data class Person(val id: Long, val name: String)

fun main() {
    SimpleApplication().run()
}
