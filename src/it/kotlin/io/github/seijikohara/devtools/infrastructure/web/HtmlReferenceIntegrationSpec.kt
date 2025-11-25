package io.github.seijikohara.devtools.infrastructure.web

import io.github.seijikohara.devtools.infrastructure.web.dto.HtmlEntitySearchResponseDto
import io.kotest.assertions.assertSoftly
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

/**
 * Integration tests for HTML Reference endpoints.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@ApplyExtension(SpringExtension::class)
class HtmlReferenceIntegrationSpec(
    private val webTestClient: WebTestClient,
) : FunSpec({

        context("HTML entities endpoint") {
            test("GET /api/html-entities should return paginated results") {
                val response =
                    webTestClient
                        .get()
                        .uri("/api/html-entities?name=&page=0&size=10")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<HtmlEntitySearchResponseDto>()
                        .returnResult()
                        .responseBody

                assertSoftly(response) {
                    it shouldNotBe null
                    it!!.content.shouldNotBeEmpty()
                    it.totalElements shouldBeGreaterThan 0
                    it.page shouldBe 0
                    it.size shouldBe 10
                }
            }

            test("GET /api/html-entities should filter by name") {
                val response =
                    webTestClient
                        .get()
                        .uri("/api/html-entities?name=nbsp&page=0&size=10")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<HtmlEntitySearchResponseDto>()
                        .returnResult()
                        .responseBody

                assertSoftly(response) {
                    it shouldNotBe null
                    it!!.content.shouldNotBeEmpty()

                    // All returned content should have "nbsp" in their name
                    it.content.forEach { item ->
                        item.name.contains("nbsp", ignoreCase = true) shouldBe true
                    }
                }
            }

            test("GET /api/html-entities should handle pagination") {
                val firstPage =
                    webTestClient
                        .get()
                        .uri("/api/html-entities?name=&page=0&size=5")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<HtmlEntitySearchResponseDto>()
                        .returnResult()
                        .responseBody

                val secondPage =
                    webTestClient
                        .get()
                        .uri("/api/html-entities?name=&page=1&size=5")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<HtmlEntitySearchResponseDto>()
                        .returnResult()
                        .responseBody

                assertSoftly {
                    firstPage shouldNotBe null
                    secondPage shouldNotBe null

                    // Different pages should have different content
                    firstPage!!.content.size shouldBe 5
                    secondPage!!.content.size shouldBe 5

                    // First item of first page should have a lower code value than first item of second page
                    if (firstPage.content.isNotEmpty() && secondPage.content.isNotEmpty()) {
                        firstPage.content.first().code shouldBeLessThan secondPage.content.first().code
                    }
                }
            }

            test("GET /api/html-entities should return empty list for non-existent name") {
                val response =
                    webTestClient
                        .get()
                        .uri("/api/html-entities?name=nonexistententity123456&page=0&size=10")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<HtmlEntitySearchResponseDto>()
                        .returnResult()
                        .responseBody

                assertSoftly(response) {
                    it shouldNotBe null
                    it!!.content shouldBe emptyList()
                    it.totalElements shouldBe 0
                }
            }

            test("GET /api/html-entities should handle invalid page size") {
                webTestClient
                    .get()
                    .uri("/api/html-entities?name=&page=0&size=0")
                    .exchange()
                    .expectStatus()
                    .isBadRequest
            }

            test("GET /api/html-entities should handle invalid page number") {
                webTestClient
                    .get()
                    .uri("/api/html-entities?name=&page=-1&size=10")
                    .exchange()
                    .expectStatus()
                    .isBadRequest
            }
        }
    })
