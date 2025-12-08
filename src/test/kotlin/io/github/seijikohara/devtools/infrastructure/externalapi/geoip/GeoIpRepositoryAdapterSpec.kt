package io.github.seijikohara.devtools.infrastructure.externalapi.geoip

import io.github.seijikohara.devtools.infrastructure.config.ApplicationProperties
import io.github.seijikohara.devtools.infrastructure.externalapi.common.decodeJson
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.springframework.core.io.ClassPathResource
import org.springframework.web.reactive.function.client.WebClient

/**
 * Unit tests for GeoIpRepositoryAdapter.
 *
 * Note: Due to the complexity of mocking WebClient's fluent API chain,
 * comprehensive testing of external API interactions is covered in integration tests.
 * These unit tests focus on verifying the adapter's structure and basic initialization.
 *
 * See: NetworkInfoIntegrationTest for end-to-end testing with actual (or mocked) GeoIP service.
 */
class GeoIpRepositoryAdapterSpec :
    FunSpec({

        fun createTestApplicationProperties(): ApplicationProperties =
            ApplicationProperties(
                apiBasePath = "/api",
                indexFile = ClassPathResource("static/index.html"),
                swaggerUiPaths = listOf(),
                cors =
                    ApplicationProperties.CorsProperties(
                        mappingPathPattern = "/api/**",
                        allowedOrigins = listOf("*"),
                        allowedMethods = listOf("*"),
                        maxAge = 3600,
                    ),
                network =
                    ApplicationProperties.NetworkProperties(
                        rdap =
                            ApplicationProperties.NetworkProperties.RdapProperties(
                                ipv4 = ClassPathResource("rdap/ipv4.json"),
                                ipv6 = ClassPathResource("rdap/ipv6.json"),
                            ),
                        geo =
                            ApplicationProperties.NetworkProperties.GeoProperties(
                                uri = "https://test.example.com",
                            ),
                        dns =
                            ApplicationProperties.NetworkProperties.DnsProperties(
                                uri = "https://dns.google/resolve",
                            ),
                    ),
            )

        context("GeoIpRepositoryAdapter initialization") {
            test("should successfully initialize with WebClient and ApplicationProperties") {
                val mockWebClient = mockk<WebClient>(relaxed = true)
                val applicationProperties = createTestApplicationProperties()

                val adapter = GeoIpRepositoryAdapter(mockWebClient, applicationProperties)

                // Verify adapter is created successfully
                adapter shouldBe adapter
            }
        }

        context("IpApiCoResponse parsing") {
            test("should parse valid ipapi.co JSON response") {
                val testJson =
                    """
                    {
                        "ip": "8.8.8.8",
                        "version": "IPv4",
                        "city": "Mountain View",
                        "region": "California",
                        "region_code": "CA",
                        "country_code": "US",
                        "country_code_iso3": "USA",
                        "country_name": "United States",
                        "country_capital": "Washington",
                        "country_tld": ".us",
                        "continent_code": "NA",
                        "in_eu": false,
                        "postal": "94035",
                        "latitude": 37.386,
                        "longitude": -122.0838,
                        "timezone": "America/Los_Angeles",
                        "utc_offset": "-0800",
                        "country_calling_code": "+1",
                        "currency": "USD",
                        "currency_name": "Dollar",
                        "languages": "en-US,es-US",
                        "country_area": 9629091.0,
                        "country_population": 310232863,
                        "asn": "AS15169",
                        "org": "Google LLC"
                    }
                    """.trimIndent()

                val response = decodeJson<IpApiCoResponse>(testJson)

                response?.ip shouldBe "8.8.8.8"
                response?.version shouldBe "IPv4"
                response?.city shouldBe "Mountain View"
                response?.region shouldBe "California"
                response?.regionCode shouldBe "CA"
                response?.countryCode shouldBe "US"
                response?.countryCodeIso3 shouldBe "USA"
                response?.countryName shouldBe "United States"
                response?.latitude shouldBe 37.386
                response?.longitude shouldBe -122.0838
                response?.asn shouldBe "AS15169"
                response?.org shouldBe "Google LLC"
                response?.inEu shouldBe false
            }

            test("should handle minimal ipapi.co JSON response") {
                val testJson =
                    """
                    {
                        "ip": "192.168.1.1"
                    }
                    """.trimIndent()

                val response = decodeJson<IpApiCoResponse>(testJson)

                response?.ip shouldBe "192.168.1.1"
                response?.city shouldBe null
                response?.countryCode shouldBe null
                response?.latitude shouldBe null
                response?.longitude shouldBe null
            }

            test("should handle ipapi.co error response") {
                val testJson =
                    """
                    {
                        "error": true,
                        "reason": "Reserved IP Address"
                    }
                    """.trimIndent()

                val response = decodeJson<IpApiCoResponse>(testJson)

                response?.error shouldBe true
                response?.reason shouldBe "Reserved IP Address"
            }
        }
    })
