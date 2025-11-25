package io.github.seijikohara.devtools.infrastructure.web

import io.github.seijikohara.devtools.infrastructure.web.dto.GeoResponseDto
import io.github.seijikohara.devtools.infrastructure.web.dto.RdapResponseDto
import io.kotest.assertions.assertSoftly
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

/**
 * Integration tests for Network Info endpoints.
 *
 * Note: These tests involve external API calls (RDAP and GeoIP services),
 * so they may be slower and potentially less stable than other integration tests.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@ApplyExtension(SpringExtension::class)
class NetworkInfoIntegrationSpec(
    private val webTestClient: WebTestClient,
) : FunSpec({

        context("RDAP endpoint") {
            withData(
                nameFn = { "GET /api/rdap/$it should return 400 for invalid IP address" },
                "invalid-ip",
                "not-an-ip",
                "256.256.256.256",
            ) { invalidIp ->
                webTestClient
                    .get()
                    .uri("/api/rdap/$invalidIp")
                    .exchange()
                    .expectStatus()
                    .isBadRequest
            }

            xtest("GET /api/rdap/{ip} should return 200 for valid IPv4 address") {
                // Disabled: This test depends on external RDAP API which may be unavailable or slow
                // Integration tests should focus on testing the integration between application layers,
                // not external API availability
                val response =
                    webTestClient
                        .get()
                        .uri("/api/rdap/8.8.8.8")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<RdapResponseDto>()
                        .returnResult()
                        .responseBody

                assertSoftly(response) {
                    it shouldNotBe null
                    it!!.ipAddress shouldBe "8.8.8.8"
                }
            }

            xtest("GET /api/rdap/{ip} should return 200 for valid IPv6 address") {
                // Disabled: This test depends on external RDAP API which may be unavailable or slow
                // Integration tests should focus on testing the integration between application layers,
                // not external API availability
                val response =
                    webTestClient
                        .get()
                        .uri("/api/rdap/2001:4860:4860::8888")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<RdapResponseDto>()
                        .returnResult()
                        .responseBody

                assertSoftly(response) {
                    it shouldNotBe null
                    it!!.ipAddress shouldBe "2001:4860:4860::8888"
                }
            }

            test("GET /api/rdap/{ip} should return 404 when RDAP server not found") {
                // Private IP addresses (RFC 1918) do not have RDAP servers
                webTestClient
                    .get()
                    .uri("/api/rdap/192.168.1.1")
                    .exchange()
                    .expectStatus()
                    .isNotFound
            }

            test("GET /api/rdap/{ip} should return 404 for loopback address") {
                // Loopback addresses (127.0.0.0/8) do not have RDAP servers
                webTestClient
                    .get()
                    .uri("/api/rdap/127.0.0.1")
                    .exchange()
                    .expectStatus()
                    .isNotFound
            }
        }

        context("GeoLocation endpoint") {
            withData(
                nameFn = { "GET /api/geo/$it should return 400 for invalid IP address" },
                "invalid-ip",
                "not-an-ip",
                "256.256.256.256",
            ) { invalidIp ->
                webTestClient
                    .get()
                    .uri("/api/geo/$invalidIp")
                    .exchange()
                    .expectStatus()
                    .isBadRequest
            }

            xtest("GET /api/geo/{ip} should return 200 for valid IPv4 address") {
                // Disabled: This test depends on external GeoIP API which may be unavailable or slow
                // Integration tests should focus on testing the integration between application layers,
                // not external API availability
                val response =
                    webTestClient
                        .get()
                        .uri("/api/geo/8.8.8.8")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<GeoResponseDto>()
                        .returnResult()
                        .responseBody

                assertSoftly(response) {
                    it shouldNotBe null
                    it!!.ipAddress shouldBe "8.8.8.8"
                }
            }

            xtest("GET /api/geo/{ip} should return location data for valid IP") {
                // Disabled: This test depends on external API response which may vary
                val response =
                    webTestClient
                        .get()
                        .uri("/api/geo/8.8.8.8")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<GeoResponseDto>()
                        .returnResult()
                        .responseBody

                assertSoftly(response) {
                    it shouldNotBe null
                    it!!.ipAddress shouldBe "8.8.8.8"
                    it.countryCode shouldNotBe null
                }
            }
        }
    })
