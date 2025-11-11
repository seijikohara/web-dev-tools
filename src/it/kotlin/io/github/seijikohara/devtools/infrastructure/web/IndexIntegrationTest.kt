package io.github.seijikohara.devtools.infrastructure.web

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
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
 * Integration tests for Index/Dashboard endpoint.
 *
 * Tests the catch-all route that serves the Vue.js SPA.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@ApplyExtension(SpringExtension::class)
class IndexIntegrationTest(
    private val webTestClient: WebTestClient,
) : FunSpec({

        context("Route handling") {
            test("GET / should return HTML with 200 OK") {
                webTestClient
                    .get()
                    .uri("/")
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectHeader()
                    .contentType(MediaType.TEXT_HTML)
            }

            test("GET /dashboard should return HTML with 200 OK") {
                webTestClient
                    .get()
                    .uri("/dashboard")
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectHeader()
                    .contentType(MediaType.TEXT_HTML)
            }

            test("GET /any-spa-route should return HTML with 200 OK") {
                // SPA routing - any non-API path should return index.html
                webTestClient
                    .get()
                    .uri("/any-spa-route")
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectHeader()
                    .contentType(MediaType.TEXT_HTML)
            }
        }

        context("HTML content validation") {
            test("Index HTML should contain Vue.js app root element") {
                val responseBody =
                    webTestClient
                        .get()
                        .uri("/")
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
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody(String::class.java)
                        .returnResult()
                        .responseBody

                responseBody shouldNotBe null

                // Verify that common sensitive keywords are not exposed
                val sensitiveKeywords = listOf("password", "secret", "api_key", "apikey", "token")
                val lowerCaseBody = responseBody!!.lowercase()
                sensitiveKeywords.forEach { keyword ->
                    lowerCaseBody.contains(keyword) shouldBe false
                }
            }
        }
    })
