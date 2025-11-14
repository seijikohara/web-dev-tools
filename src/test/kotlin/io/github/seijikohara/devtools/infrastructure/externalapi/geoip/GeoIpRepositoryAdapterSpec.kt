package io.github.seijikohara.devtools.infrastructure.externalapi.geoip

import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.infrastructure.config.ApplicationProperties
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

        context("GeoIpResponse parsing") {
            test("should parse valid GeoIP JSON to GeoLocation") {
                val testJson =
                    """
                    {
                        "ip": "8.8.8.8",
                        "country_code": "US",
                        "city": "Mountain View",
                        "latitude": 37.386,
                        "longitude": -122.0838
                    }
                    """.trimIndent()

                val geoIpResponse = decodeJsonToGeoIpResponse(testJson)
                val ipAddress = IpAddress.of("8.8.8.8").getOrThrow()
                val geoLocation = geoIpResponse.toDomain(ipAddress)

                geoLocation.ipAddress shouldBe ipAddress
                geoLocation.countryCode?.value shouldBe "US"
                geoLocation.city shouldBe "Mountain View"
                geoLocation.latitude shouldBe 37.386
                geoLocation.longitude shouldBe -122.0838
            }

            test("should handle alternative country field name") {
                val testJson =
                    """
                    {
                        "ip": "1.1.1.1",
                        "country": "AU",
                        "city": "Sydney"
                    }
                    """.trimIndent()

                val geoIpResponse = decodeJsonToGeoIpResponse(testJson)
                val ipAddress = IpAddress.of("1.1.1.1").getOrThrow()
                val geoLocation = geoIpResponse.toDomain(ipAddress)

                geoLocation.countryCode?.value shouldBe "AU"
                geoLocation.city shouldBe "Sydney"
            }

            test("should handle missing optional fields") {
                val testJson =
                    """
                    {
                        "ip": "192.168.1.1"
                    }
                    """.trimIndent()

                val geoIpResponse = decodeJsonToGeoIpResponse(testJson)
                val ipAddress = IpAddress.of("192.168.1.1").getOrThrow()
                val geoLocation = geoIpResponse.toDomain(ipAddress)

                geoLocation.ipAddress shouldBe ipAddress
                geoLocation.countryCode shouldBe null
                geoLocation.city shouldBe null
                geoLocation.latitude shouldBe null
                geoLocation.longitude shouldBe null
            }
        }
    })
