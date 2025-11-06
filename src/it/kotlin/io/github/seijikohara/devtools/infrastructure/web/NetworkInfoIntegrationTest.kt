package io.github.seijikohara.devtools.infrastructure.web

import io.github.seijikohara.devtools.infrastructure.web.dto.GeoResponseDto
import io.github.seijikohara.devtools.infrastructure.web.dto.RdapResponseDto
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
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
class NetworkInfoIntegrationTest(
    private val webTestClient: WebTestClient,
) : FunSpec({

        context("RDAP endpoint") {
            test("GET /api/rdap/{ip} should return 400 for invalid IP address") {
                webTestClient
                    .get()
                    .uri("/api/rdap/invalid-ip")
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

                response shouldNotBe null
                response!!.rdap shouldNotBe null
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

                response shouldNotBe null
                response!!.rdap shouldNotBe null
            }
        }

        context("GeoLocation endpoint") {
            test("GET /api/geo/{ip} should return 400 for invalid IP address") {
                webTestClient
                    .get()
                    .uri("/api/geo/invalid-ip")
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

                response shouldNotBe null
                response!!.geo shouldNotBe null
            }

            xtest("GET /api/geo/{ip} should return location data for valid IP") {
                // Disabled: This test depends on external API response which may vary
                // and the DTO structure uses raw JSON data without direct property access
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

                response shouldNotBe null
                response!!.geo shouldNotBe null
            }
        }
    })
