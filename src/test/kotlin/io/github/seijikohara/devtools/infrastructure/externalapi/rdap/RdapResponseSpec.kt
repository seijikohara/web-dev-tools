package io.github.seijikohara.devtools.infrastructure.externalapi.rdap

import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

class RdapResponseSpec :
    FunSpec({

        val testIpAddress = IpAddress.of("192.0.2.1").getOrThrow()

        context("RdapResponse.toDomain()") {
            test("should convert complete RDAP response to RdapInformation") {
                val responseData: Map<String, JsonElement> =
                    mapOf(
                        "handle" to JsonPrimitive("NET-192-0-2-0-1"),
                        "name" to JsonPrimitive("TEST-NET-1"),
                        "country" to JsonPrimitive("US"),
                    )
                val response = RdapResponse(responseData)

                val result = response.toDomain(testIpAddress)

                assertSoftly(result) {
                    it.ipAddress shouldBe testIpAddress
                    it.handle shouldBe "NET-192-0-2-0-1"
                    it.name shouldBe "TEST-NET-1"
                    it.country shouldNotBe null
                    it.country?.value shouldBe "US"
                    it.registeredAt shouldBe null
                }
            }

            test("should handle response with missing optional fields") {
                val responseData: Map<String, JsonElement> =
                    mapOf(
                        "handle" to JsonPrimitive("NET-10-0-0-0-1"),
                    )
                val response = RdapResponse(responseData)

                val result = response.toDomain(testIpAddress)

                assertSoftly(result) {
                    it.ipAddress shouldBe testIpAddress
                    it.handle shouldBe "NET-10-0-0-0-1"
                    it.name shouldBe null
                    it.country shouldBe null
                    it.registeredAt shouldBe null
                }
            }

            test("should handle response with all fields missing") {
                val response = RdapResponse(emptyMap())

                val result = response.toDomain(testIpAddress)

                assertSoftly(result) {
                    it.ipAddress shouldBe testIpAddress
                    it.handle shouldBe null
                    it.name shouldBe null
                    it.country shouldBe null
                    it.registeredAt shouldBe null
                }
            }

            test("should handle invalid country code") {
                val responseData: Map<String, JsonElement> =
                    mapOf(
                        "handle" to JsonPrimitive("NET-TEST"),
                        "name" to JsonPrimitive("Test Network"),
                        "country" to JsonPrimitive("INVALID"),
                    )
                val response = RdapResponse(responseData)

                val result = response.toDomain(testIpAddress)

                assertSoftly(result) {
                    it.handle shouldBe "NET-TEST"
                    it.name shouldBe "Test Network"
                    it.country shouldBe null
                }
            }

            test("should correctly extract fields from response with custom fields") {
                val responseData: Map<String, JsonElement> =
                    mapOf(
                        "handle" to JsonPrimitive("NET-EXAMPLE"),
                        "name" to JsonPrimitive("Example Network"),
                        "country" to JsonPrimitive("JP"),
                        "custom_field" to JsonPrimitive("custom_value"),
                        "numeric_field" to JsonPrimitive(999),
                    )
                val response = RdapResponse(responseData)

                val result = response.toDomain(testIpAddress)

                assertSoftly(result) {
                    it.ipAddress shouldBe testIpAddress
                    it.handle shouldBe "NET-EXAMPLE"
                    it.name shouldBe "Example Network"
                    it.country shouldNotBe null
                    it.country?.value shouldBe "JP"
                }
            }

            test("should handle different IP address types") {
                val ipv6Address = IpAddress.of("2001:db8::1").getOrThrow()
                val responseData: Map<String, JsonElement> =
                    mapOf(
                        "handle" to JsonPrimitive("NET6-2001-DB8-1"),
                        "name" to JsonPrimitive("IPv6 Test"),
                        "country" to JsonPrimitive("EU"),
                    )
                val response = RdapResponse(responseData)

                val result = response.toDomain(ipv6Address)

                assertSoftly(result) {
                    it.ipAddress shouldBe ipv6Address
                    it.handle shouldBe "NET6-2001-DB8-1"
                }
            }
        }
    })
