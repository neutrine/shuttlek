package com.neutrine.shuttlek.common.serializer

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.time.*
import java.util.*
import kotlin.test.assertEquals

internal class JsonSerializerSpec: Spek({
    describe("A JsonSerializer") {
        describe("serialize") {
            it("should serialize simpleObject") {
                assertEquals("""{"string":"Simple string","int":42,"double":3.14}""", String(JsonSerializer.serialize(simpleObject)))
            }

            it("should serialize Date as epochMillis") {
                assertEquals("1573526183", String(JsonSerializer.serialize(Date(1573526183))))
            }

            it("should serialize LocalDate as epochDay") {
                assertEquals("18201", String(JsonSerializer.serialize(LocalDate.parse("2019-11-01"))))
            }

            it("should serialize LocalTime as millis after midnight") {
                assertEquals("36243000", String(JsonSerializer.serialize(LocalTime.of(10, 4, 3, 30))))
            }

            it("should serialize LocalDateTime as epochMillis") {
                assertEquals("1573524962000", String(JsonSerializer.serialize(LocalDateTime.parse("2019-11-12T02:16:02"))))
            }

            it("should serialize OffsetTime as millis after midnight") {
                assertEquals("33330000", String(JsonSerializer.serialize(OffsetTime.parse("10:15:30-01:00"))))
            }

            it("should serialize OffsetDateTime as epochMillis") {
                assertEquals("1573532162000", String(JsonSerializer.serialize(OffsetDateTime.parse("2019-11-12T02:16:02-02:00"))))
            }

            it("should serialize ZonedDateTime as epochMillis") {
                assertEquals("1573532162000", String(JsonSerializer.serialize(ZonedDateTime.parse("2019-11-12T02:16:02-02:00"))))
            }
        }
    }
})
internal object simpleObject {
    val string = "Simple string"
    val int = 42
    val double = 3.14
}