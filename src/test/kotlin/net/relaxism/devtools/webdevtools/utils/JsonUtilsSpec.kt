package net.relaxism.devtools.webdevtools.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.Serializable

class JsonUtilsSpec :
    FunSpec({

        @Serializable
        data class TestData(
            val name: String,
            val value: Int,
        )

        test("fromJson with type parameter should handle various inputs") {
            forAll(
                row("""{"name":"test","value":123}""", true, "valid JSON"),
                row("", false, "empty string"),
                row(null, false, "null string"),
                row("   ", false, "whitespace only"),
            ) { json, shouldSucceed, description ->
                val result = JsonUtils.fromJson<TestData>(json)
                if (shouldSucceed) {
                    result shouldNotBe null
                    result!!.name shouldBe "test"
                    result.value shouldBe 123
                } else {
                    result shouldBe null
                }
            }
        }

        test("fromJson without type parameter should handle various JSON inputs") {
            forAll(
                row("""{"name":"test","value":123}""", mapOf("name" to "test", "value" to 123), "simple JSON"),
                row("", emptyMap(), "empty string"),
                row(null, emptyMap(), "null string"),
                row("   ", emptyMap(), "whitespace only"),
            ) { json, expected, description ->
                val result = JsonUtils.fromJson(json)
                if (expected.isEmpty()) {
                    result shouldBe emptyMap()
                } else {
                    result shouldNotBe null
                    expected.forEach { (key, value) ->
                        result[key] shouldBe value
                    }
                }
            }
        }

        test("toJson should serialize objects") {
            forAll(
                row(TestData("test", 123), """{"name":"test","value":123}""", "simple object"),
                row(TestData("hello", 456), """{"name":"hello","value":456}""", "different values"),
            ) { data, expected, description ->
                val result = JsonUtils.toJson(data)
                result shouldNotBe null
                result shouldBe expected
            }
        }

        test("fromJson should handle complex JSON structures") {
            val json = """{"user":{"name":"john","age":30},"items":[1,2,3]}"""
            val result = JsonUtils.fromJson(json)

            result shouldNotBe null
            result["user"] shouldNotBe null
            result["items"] shouldNotBe null

            val user = result["user"] as Map<*, *>
            user["name"] shouldBe "john"
            user["age"] shouldBe 30

            val items = result["items"] as List<*>
            items.size shouldBe 3
            items[0] shouldBe 1
        }
    })
