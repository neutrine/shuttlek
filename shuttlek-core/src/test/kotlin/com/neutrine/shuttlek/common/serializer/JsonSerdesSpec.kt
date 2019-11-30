package com.neutrine.shuttlek.common.serializer

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.neutrine.shuttlek.common.serdes.JsonSerdes
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Date
import kotlin.test.assertEquals

internal class JsonSerdesSpec : Spek({
    describe("A JsonSerdes") {
        describe("serialize") {
            it("should serialize simpleObject") {
                assertEquals("""{"double":3.14,"int":42,"string":"Simple string"}""", String(JsonSerdes.serialize(SimpleObject)))
            }

            it("should serialize Date as epochMillis") {
                assertEquals("1573526183", String(JsonSerdes.serialize(Date(1573526183))))
            }

            it("should serialize LocalDate as epochDay") {
                assertEquals("18201", String(JsonSerdes.serialize(LocalDate.parse("2019-11-01"))))
            }

            it("should serialize LocalTime as millis after midnight") {
                assertEquals("36243000", String(JsonSerdes.serialize(LocalTime.of(10, 4, 3, 30))))
            }

            it("should serialize LocalDateTime as epochMillis") {
                assertEquals("1573524962000", String(JsonSerdes.serialize(LocalDateTime.parse("2019-11-12T02:16:02"))))
            }
        }
        describe("deserialize") {
            it("should deserialize simpleObject") {
                val obj = JsonSerdes.deserialize("""{"string":"Simple string","int":42,"double":3.14}""".toByteArray(), SimpleObject::class)
                assertEquals("Simple string", obj.string)
                assertEquals(42, obj.int)
                assertEquals(3.14, obj.double)
            }

            it("should deserialize epochMillis to Date") {
                assertEquals(Date(1573526183), JsonSerdes.deserialize("1573526183".toByteArray(), Date::class))
            }

            it("should deserialize epochDay to LocalDate") {
                assertEquals(LocalDate.parse("2019-11-01"), JsonSerdes.deserialize("18201".toByteArray(), LocalDate::class))
            }

            it("should deserialize millis after midnight to LocalTime") {
                assertEquals(LocalTime.of(11, 44, 33, 0), JsonSerdes.deserialize("42273000".toByteArray(), LocalTime::class))
            }

            it("should deserialize epochMillis to LocalDateTime") {
                assertEquals(LocalDateTime.parse("2019-11-12T02:16:02"), JsonSerdes.deserialize("1573524962000".toByteArray(), LocalDateTime::class))
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
