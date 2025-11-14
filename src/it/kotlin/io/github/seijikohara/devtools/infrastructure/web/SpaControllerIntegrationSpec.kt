package io.github.seijikohara.devtools.infrastructure.web

import io.kotest.assertions.assertSoftly
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

/**
 * Integration tests for SPA Fallback Error Handler.
 *
 * Tests the error-based fallback mechanism that serves the Vue.js SPA
 * for 404 errors with HTML Accept headers, while correctly handling
 * API endpoint 404s.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@ApplyExtension(SpringExtension::class)
class SpaControllerIntegrationSpec(
    private val webTestClient: WebTestClient,
) : FunSpec({

        context("Route handling") {
            withData(
                nameFn = { "GET $it should return HTML with 200 OK" },
                "/",
                "/dashboard",
                "/any-spa-route",
            ) { path ->
                webTestClient
                    .get()
                    .uri(path)
                    .accept(MediaType.TEXT_HTML)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectHeader()
                    .contentType(MediaType.TEXT_HTML)
            }
        }

        context("API endpoint 404 handling") {
            withData(
                nameFn = { "GET $it should return 404 error (not SPA)" },
                "/api/nonexistent",
                "/v3/api-docs/nonexistent",
                "/swagger-ui/nonexistent",
                "/actuator/nonexistent",
            ) { path ->
                webTestClient
                    .get()
                    .uri(path)
                    .accept(MediaType.TEXT_HTML)
                    .exchange()
                    .expectStatus()
                    .isNotFound
            }

            test("GET /api/nonexistent with JSON Accept should return 404 error") {
                webTestClient
                    .get()
                    .uri("/api/nonexistent")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isNotFound
                // Note: Spring WebFlux's default error handler may not set Content-Type
                // The important part is that it returns 404, not the SPA index.html
            }
        }

        context("HTML content validation") {
            test("Index HTML should contain Vue.js app root element") {
                val responseBody =
                    webTestClient
                        .get()
                        .uri("/")
                        .accept(MediaType.TEXT_HTML)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(String::class.java)
                        .returnResult()
                        .responseBody

                // Vue.js app typically mounts to #app or similar
                responseBody shouldContain "<div id=\"app\">"
            }

            test("Index HTML should not expose sensitive information") {
                val responseBody =
                    webTestClient
                        .get()
                        .uri("/")
                        .accept(MediaType.TEXT_HTML)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(String::class.java)
                        .returnResult()
                        .responseBody

                assertSoftly {
                    responseBody shouldNotBe null

                    // Verify that common sensitive keywords are not exposed
                    val sensitiveKeywords = listOf("password", "secret", "api_key", "apikey", "token")
                    val lowerCaseBody = responseBody!!.lowercase()
                    sensitiveKeywords.forEach { keyword ->
                        lowerCaseBody.contains(keyword) shouldBe false
                    }
                }
            }
        }
    })
