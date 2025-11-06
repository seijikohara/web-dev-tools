package io.github.seijikohara.devtools.infrastructure.web

import io.github.seijikohara.devtools.infrastructure.web.dto.HttpHeadersResponseDto
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

/**
 * Integration tests for HTTP Diagnostics endpoints.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@ApplyExtension(SpringExtension::class)
class HttpDiagnosticsIntegrationTest(
    private val webTestClient: WebTestClient,
) : FunSpec({

    test("GET /api/http-headers should return request headers") {
        val response =
            webTestClient
                .get()
                .uri("/api/http-headers")
                .header("X-Custom-Header", "test-value")
                .exchange()
                .expectStatus().isOk
                .expectBody<HttpHeadersResponseDto>()
                .returnResult()
                .responseBody

        response shouldNotBe null
        response!!.headers.shouldNotBeEmpty()

        // Verify that the custom header is included in the response
        response.headers shouldContain
            HttpHeadersResponseDto.HttpHeaderDto(
                name = "X-Custom-Header",
                value = "test-value",
            )
    }

    test("GET /api/http-headers should include WebTestClient headers") {
        val response =
            webTestClient
                .get()
                .uri("/api/http-headers")
                .exchange()
                .expectStatus().isOk
                .expectBody<HttpHeadersResponseDto>()
                .returnResult()
                .responseBody

        response shouldNotBe null
        response!!.headers.shouldNotBeEmpty()

        // WebTestClient should send Request ID header
        val headerNames = response.headers.map { it.name }
        headerNames shouldContain "WebTestClient-Request-Id"
    }
})
