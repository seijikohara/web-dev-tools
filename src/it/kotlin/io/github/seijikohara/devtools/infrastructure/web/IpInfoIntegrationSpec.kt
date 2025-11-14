package io.github.seijikohara.devtools.infrastructure.web

import io.github.seijikohara.devtools.infrastructure.web.dto.IpInfoResponseDto
import io.kotest.assertions.assertSoftly
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
 * Integration tests for IP Info endpoint.
 *
 * Tests the /api/ip endpoint which returns client IP address and hostname information.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@ApplyExtension(SpringExtension::class)
class IpInfoIntegrationSpec(
    private val webTestClient: WebTestClient,
) : FunSpec({

        context("IP info endpoint") {
            test("GET /api/ip should detect X-Forwarded-For header") {
                val testIp = "203.0.113.1"
                val response =
                    webTestClient
                        .get()
                        .uri("/api/ip")
                        .header("X-Forwarded-For", "$testIp, 198.51.100.1")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<IpInfoResponseDto>()
                        .returnResult()
                        .responseBody

                assertSoftly(response) {
                    it shouldNotBe null
                    it!!.ipAddress shouldBe testIp
                }
            }

            test("GET /api/ip should detect X-Real-IP header") {
                val testIp = "198.51.100.1"
                val response =
                    webTestClient
                        .get()
                        .uri("/api/ip")
                        .header("X-Real-IP", testIp)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<IpInfoResponseDto>()
                        .returnResult()
                        .responseBody

                assertSoftly(response) {
                    it shouldNotBe null
                    it!!.ipAddress shouldBe testIp
                }
            }

            test("GET /api/ip should prioritize X-Forwarded-For over X-Real-IP") {
                val forwardedIp = "203.0.113.1"
                val realIp = "198.51.100.1"
                val response =
                    webTestClient
                        .get()
                        .uri("/api/ip")
                        .header("X-Forwarded-For", forwardedIp)
                        .header("X-Real-IP", realIp)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<IpInfoResponseDto>()
                        .returnResult()
                        .responseBody

                assertSoftly(response) {
                    it shouldNotBe null
                    it!!.ipAddress shouldBe forwardedIp
                }
            }

            test("GET /api/ip should return response with valid DTO structure") {
                // Test without headers to verify basic response structure
                // In test environment, remoteAddress might be null, but the endpoint should still work
                webTestClient
                    .get()
                    .uri("/api/ip")
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectBody<IpInfoResponseDto>()
            }
        }
    })
