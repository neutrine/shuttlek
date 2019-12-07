package com.neutrine.shuttlek.common

import org.apache.kafka.common.header.internals.RecordHeaders
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

internal object HeadersExtensionsSpec : Spek({
    describe("A RecordHeaders") {
        val headers by memoized { RecordHeaders() }

        describe("valueAsString") {
            val key = "theKey"
            val value = "42".toByteArray()

            it("should return value as string for a simple value") {
                headers.add(key, value)
                assertEquals(String(value), headers.valueAsString(key))
            }

            it("should return last value as string for a list value") {
                headers.add(key, "FirstValue".toByteArray())
                headers.add(key, value)
                assertEquals(String(value), headers.valueAsString(key))
            }

            it("should return null if the key don't exists") {
                assertEquals(null, headers.valueAsString(key))
            }
        }
    }
})
