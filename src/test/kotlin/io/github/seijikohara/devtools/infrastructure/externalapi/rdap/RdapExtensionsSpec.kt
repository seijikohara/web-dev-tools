package io.github.seijikohara.devtools.infrastructure.externalapi.rdap

import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

class RdapExtensionsSpec :
    FunSpec({

        val testIpAddress = IpAddress.of("192.0.2.1").getOrThrow()

        context("Map<String, JsonElement>.toRdapInformation()") {
            test("should convert complete RDAP response to RdapInformation") {
                val response: Map<String, JsonElement> =
                    mapOf(
                        "handle" to JsonPrimitive("NET-192-0-2-0-1"),
                        "name" to JsonPrimitive("TEST-NET-1"),
                        "country" to JsonPrimitive("US"),
                    )

                val result = response.toRdapInformation(testIpAddress)

                result.ipAddress shouldBe testIpAddress
                result.handle shouldBe "NET-192-0-2-0-1"
                result.name shouldBe "TEST-NET-1"
                result.country shouldNotBe null
                result.country?.value shouldBe "US"
                result.registeredAt shouldBe null
                result.rawData shouldBe response
            }

            test("should handle response with missing optional fields") {
                val response: Map<String, JsonElement> =
                    mapOf(
                        "handle" to JsonPrimitive("NET-10-0-0-0-1"),
                    )

                val result = response.toRdapInformation(testIpAddress)

                result.ipAddress shouldBe testIpAddress
                result.handle shouldBe "NET-10-0-0-0-1"
                result.name shouldBe null
                result.country shouldBe null
                result.registeredAt shouldBe null
            }

            test("should handle response with all fields missing") {
                val response: Map<String, JsonElement> = emptyMap()

                val result = response.toRdapInformation(testIpAddress)

                result.ipAddress shouldBe testIpAddress
                result.handle shouldBe null
                result.name shouldBe null
                result.country shouldBe null
                result.registeredAt shouldBe null
                result.rawData shouldBe response
            }

            test("should handle invalid country code") {
                val response: Map<String, JsonElement> =
                    mapOf(
                        "handle" to JsonPrimitive("NET-TEST"),
                        "name" to JsonPrimitive("Test Network"),
                        "country" to JsonPrimitive("INVALID"),
                    )

                val result = response.toRdapInformation(testIpAddress)

                result.handle shouldBe "NET-TEST"
                result.name shouldBe "Test Network"
                result.country shouldBe null
            }

            test("should preserve raw data for debugging") {
                val response: Map<String, JsonElement> =
                    mapOf(
                        "handle" to JsonPrimitive("NET-EXAMPLE"),
                        "name" to JsonPrimitive("Example Network"),
                        "country" to JsonPrimitive("JP"),
                        "custom_field" to JsonPrimitive("custom_value"),
                        "numeric_field" to JsonPrimitive(999),
                    )

                val result = response.toRdapInformation(testIpAddress)

                result.rawData shouldBe response
                result.rawData["custom_field"] shouldBe JsonPrimitive("custom_value")
                result.rawData["numeric_field"] shouldBe JsonPrimitive(999)
            }

            test("should handle different IP address types") {
                val ipv6Address = IpAddress.of("2001:db8::1").getOrThrow()
                val response: Map<String, JsonElement> =
                    mapOf(
                        "handle" to JsonPrimitive("NET6-2001-DB8-1"),
                        "name" to JsonPrimitive("IPv6 Test"),
                        "country" to JsonPrimitive("EU"),
                    )

                val result = response.toRdapInformation(ipv6Address)

                result.ipAddress shouldBe ipv6Address
                result.handle shouldBe "NET6-2001-DB8-1"
            }
        }
    })
