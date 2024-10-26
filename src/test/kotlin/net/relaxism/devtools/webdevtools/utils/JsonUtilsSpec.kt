package net.relaxism.devtools.webdevtools.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe

class JsonUtilsSpec : StringSpec() {
    init {
        "JSON to Object" {
            forAll(
                table(
                    headers("value", "valueType", "expected"),
                    row(null, TestData::class.java, null),
                    row("{}", TestData::class.java, TestData(0)),
                    row("{ \"a\": 1 }", TestData::class.java, TestData(1)),
                ),
            ) { value: String?, valueType: Class<*>, expected: Any? ->
                JsonUtils.fromJson(value, valueType) shouldBe expected
            }
        }

        "JSON to Map" {
            forAll(
                table(
                    headers("value", "expected"),
                    row(null, emptyMap()),
                    row("{}", emptyMap()),
                    row("{ \"a\": 1 }", mapOf("a" to 1)),
                ),
            ) { value: String?, expected: Map<String, Any> ->
                JsonUtils.fromJson(value) shouldBe expected
            }
        }

        "Map to JSON" {
            forAll(
                table(
                    headers("value", "expected"),
                    row(null, ""),
                    row(mapOf<String, Any>(), "{}"),
                    row(mapOf("a" to 1), "{\"a\":1}"),
                ),
            ) { value: Any?, expected: String ->
                JsonUtils.toJson(value) shouldBe expected
            }
        }
    }

    data class TestData(
        val a: Int,
    )
}
