package net.relaxism.devtools.webdevtools.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable

class JsonUtilsSpec : StringSpec() {
    init {
        "JSON to Object" {
            forAll(
                table(
                    headers("value", "expected"),
                    row(null, null),
                    row("{}", TestData(0)),
                    row("{ \"a\": 1 }", TestData(1)),
                ),
            ) { value: String?, expected: TestData? ->
                JsonUtils.fromJson<TestData>(value) shouldBe expected
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

        "Object to JSON" {
            forAll(
                table(
                    headers("value", "expected"),
                    row(TestData(0), "{}"),
                    row(TestData(1), "{\"a\":1}"),
                ),
            ) { value: TestData, expected: String ->
                JsonUtils.toJson(value) shouldBe expected
            }
        }
    }

    @Serializable
    data class TestData(
        val a: Int = 0,
    )
}
