package io.github.seijikohara.devtools.infrastructure.externalapi.geoip

import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

class GeoIpExtensionsSpec :
    FunSpec({

        val testIpAddress = IpAddress.of("192.0.2.1").getOrThrow()

        context("Map<String, JsonElement>.toGeoLocation()") {
            test("should convert complete GeoIP response to GeoLocation") {
                val response: Map<String, JsonElement> =
                    mapOf(
                        "country_code" to JsonPrimitive("US"),
                        "city" to JsonPrimitive("New York"),
                        "latitude" to JsonPrimitive("40.7128"),
                        "longitude" to JsonPrimitive("-74.0060"),
                    )

                val result = response.toGeoLocation(testIpAddress)

                result.ipAddress shouldBe testIpAddress
                result.countryCode shouldNotBe null
                result.countryCode?.value shouldBe "US"
                result.city shouldBe "New York"
                result.latitude shouldBe 40.7128
                result.longitude shouldBe -74.0060
                result.rawData shouldBe response
            }

            test("should handle alternative country field name") {
                val response: Map<String, JsonElement> =
                    mapOf(
                        "country" to JsonPrimitive("JP"),
                        "city" to JsonPrimitive("Tokyo"),
                        "latitude" to JsonPrimitive("35.6762"),
                        "longitude" to JsonPrimitive("139.6503"),
                    )

                val result = response.toGeoLocation(testIpAddress)

                result.countryCode shouldNotBe null
                result.countryCode?.value shouldBe "JP"
                result.city shouldBe "Tokyo"
            }

            test("should handle response with missing optional fields") {
                val response: Map<String, JsonElement> =
                    mapOf(
                        "country_code" to JsonPrimitive("GB"),
                    )

                val result = response.toGeoLocation(testIpAddress)

                result.ipAddress shouldBe testIpAddress
                result.countryCode shouldNotBe null
                result.countryCode?.value shouldBe "GB"
                result.city shouldBe null
                result.latitude shouldBe null
                result.longitude shouldBe null
            }

            test("should handle response with all fields missing") {
                val response: Map<String, JsonElement> = emptyMap()

                val result = response.toGeoLocation(testIpAddress)

                result.ipAddress shouldBe testIpAddress
                result.countryCode shouldBe null
                result.city shouldBe null
                result.latitude shouldBe null
                result.longitude shouldBe null
                result.rawData shouldBe response
            }

            test("should handle invalid coordinate values") {
                val response: Map<String, JsonElement> =
                    mapOf(
                        "country_code" to JsonPrimitive("US"),
                        "latitude" to JsonPrimitive("invalid"),
                        "longitude" to JsonPrimitive("not-a-number"),
                    )

                val result = response.toGeoLocation(testIpAddress)

                result.latitude shouldBe null
                result.longitude shouldBe null
            }

            test("should handle invalid country code") {
                val response: Map<String, JsonElement> =
                    mapOf(
                        "country_code" to JsonPrimitive("INVALID"),
                        "city" to JsonPrimitive("Unknown"),
                    )

                val result = response.toGeoLocation(testIpAddress)

                result.countryCode shouldBe null
                result.city shouldBe "Unknown"
            }

            test("should preserve raw data for debugging") {
                val response: Map<String, JsonElement> =
                    mapOf(
                        "country_code" to JsonPrimitive("CA"),
                        "custom_field" to JsonPrimitive("custom_value"),
                        "another_field" to JsonPrimitive(123),
                    )

                val result = response.toGeoLocation(testIpAddress)

                result.rawData shouldBe response
                result.rawData["custom_field"] shouldBe JsonPrimitive("custom_value")
                result.rawData["another_field"] shouldBe JsonPrimitive(123)
            }
        }
    })
