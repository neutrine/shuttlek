package com.neutrine.shuttlek.consumer

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.neutrine.shuttlek.common.serializer.SerializerType
import com.neutrine.shuttlek.common.serializer.SimpleObject
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

/**
 * @author Luiz Picanço
 */
internal object ConsumerMessageSpec : Spek({
    describe("A ConsumerMessage") {
        val valueAsJson = """{"double":3.14,"int":42,"string":"Simple string"}"""
        val consumerMessage by memoized { ConsumerMessage(3, "3", valueAsJson.toByteArray(), null, null, SerializerType.Json) }

        describe("valueAs") {
            it("should deserialize simpleObject") {
                val obj = consumerMessage.valueAs(SimpleObject::class)
                assertEquals("Simple string", obj.string)
                assertEquals(42, obj.int)
                assertEquals(3.14, obj.double)
            }
        }
    }
})

@JsonPropertyOrder(alphabetic = true)
internal object SimpleObject {
    var string = "Simple string"
    var int = 42
    var double = 3.14
}
