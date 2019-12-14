package com.neutrine.shuttlek.consumer

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal object ConsumerRecordFilterSpec : Spek({
    describe("A ConsumerRecordFilter without schemas") {
        val consumerRecordFilter by memoized { ConsumerRecordFilter() }

        describe("filter") {
            it("should return true for any schema name") {
                assertTrue(consumerRecordFilter.filter("schema"))
            }

            it("should return true if schema name is null") {
                assertTrue(consumerRecordFilter.filter(null))
            }
        }
    }

    describe("A ConsumerRecordFilter with schema names") {
        val schemaName = "someSchema"
        val consumerRecordFilter by memoized { ConsumerRecordFilter(listOf(schemaName, "oneMoreSchemaName")) }

        describe("filter") {
            it("should return true if the schema name is present") {
                assertTrue(consumerRecordFilter.filter(schemaName))
            }

            it("should return false if schema name is not present") {
                assertFalse(consumerRecordFilter.filter("notPresent"))
            }

            it("should return false if schema name is null") {
                assertFalse(consumerRecordFilter.filter(null))
            }
        }
    }
})
