package io.github.seijikohara.devtools.infrastructure.externalapi.common

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

class JsonExtensionsSpec :
    FunSpec({

        @Serializable
        data class TestData(
            val name: String,
            val value: Int,
        )

        @Serializable
        data class UserData(
            val name: String,
            val age: Int,
        )

        @Serializable
        data class NestedData(
            val user: UserData,
            val items: List<Int>,
        )

        context("decodeJson<T>() with valid JSON") {
            data class DecodeJsonCase(
                val json: String,
                val expectedName: String,
                val expectedValue: Int,
                val description: String,
            )

            withData(
                DecodeJsonCase("""{"name":"test","value":123}""", "test", 123, "standard JSON"),
                DecodeJsonCase("""{"name":"hello","value":456}""", "hello", 456, "different values"),
                DecodeJsonCase("""{"name":"","value":0}""", "", 0, "empty string and zero"),
            ) { (json, expectedName, expectedValue, _) ->
                val result = decodeJson<TestData>(json)

                result shouldNotBe null
                result!!.name shouldBe expectedName
                result.value shouldBe expectedValue
            }

            test("should handle nested objects") {
                val json = """{"user":{"name":"john","age":30},"items":[1,2,3]}"""

                val result = decodeJson<NestedData>(json)

                result shouldNotBe null
                result!!.user.name shouldBe "john"
                result.user.age shouldBe 30
                result.items shouldBe listOf(1, 2, 3)
            }

            test("should ignore unknown keys") {
                val json = """{"name":"test","value":123,"unknownField":"ignored"}"""

                val result = decodeJson<TestData>(json)

                result shouldNotBe null
                result!!.name shouldBe "test"
                result.value shouldBe 123
            }
        }

        context("decodeJson<T>() with invalid or empty input") {
            data class InvalidJsonCase(
                val json: String?,
                val description: String,
            )

            withData(
                InvalidJsonCase(null, "null string"),
                InvalidJsonCase("", "empty string"),
                InvalidJsonCase("   ", "whitespace only"),
            ) { (json, _) ->
                val result = decodeJson<TestData>(json)
                result shouldBe null
            }
        }

        context("decodeJsonToElements() with valid JSON") {
            test("should decode JSON to map of JsonElements") {
                val json = """{"name":"test","value":123,"flag":true}"""

                val result = decodeJsonToElements(json)

                result shouldNotBe null
                result.size shouldBe 3
                result["name"] shouldBe JsonPrimitive("test")
                result["value"] shouldBe JsonPrimitive(123)
                result["flag"] shouldBe JsonPrimitive(true)
            }

            test("should handle nested JSON structures") {
                val json = """{"user":{"name":"john","age":30},"items":[1,2,3]}"""

                val result = decodeJsonToElements(json)

                result shouldNotBe null
                result.size shouldBe 2
                result["user"] shouldNotBe null
                result["items"] shouldNotBe null
            }

            test("should handle empty JSON object") {
                val json = "{}"

                val result = decodeJsonToElements(json)

                result shouldNotBe null
                result.shouldBeEmpty()
            }
        }

        context("decodeJsonToElements() with invalid or empty input") {
            data class InvalidElementsCase(
                val json: String?,
                val description: String,
            )

            withData(
                InvalidElementsCase(null, "null string"),
                InvalidElementsCase("", "empty string"),
                InvalidElementsCase("   ", "whitespace only"),
            ) { (json, _) ->
                val result = decodeJsonToElements(json)
                result.shouldBeEmpty()
            }
        }

        context("decodeJsonToElements() with various data types") {
            test("should correctly decode different JSON primitive types") {
                val json =
                    """
                    {
                        "string": "hello",
                        "number": 42,
                        "float": 3.14,
                        "boolean": true,
                        "nullValue": null
                    }
                    """.trimIndent()

                val result = decodeJsonToElements(json)

                result["string"] shouldBe JsonPrimitive("hello")
                result["number"] shouldBe JsonPrimitive(42)
                result["float"] shouldBe JsonPrimitive(3.14)
                result["boolean"] shouldBe JsonPrimitive(true)
                result["nullValue"] shouldNotBe null // JsonNull is not null
            }
        }
    })
