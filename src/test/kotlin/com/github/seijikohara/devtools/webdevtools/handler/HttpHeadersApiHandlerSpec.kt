package com.github.seijikohara.devtools.webdevtools.handler

import com.github.seijikohara.devtools.webdevtools.config.ApplicationProperties
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HttpHeadersApiHandlerSpec(
    private val webTestClient: WebTestClient,
    private val applicationProperties: ApplicationProperties,
) : FunSpec({

        test("getHttpHeaders should handle various header combinations") {
            forAll(
                row(mapOf("X-Test-Header" to "test-value"), "single custom header"),
                row(mapOf("X-Custom" to "custom-value", "User-Agent" to "test-agent"), "multiple headers"),
                row(mapOf("Authorization" to "Bearer token123", "Content-Type" to "application/json"), "auth headers"),
                row(mapOf(), "no custom headers"),
            ) { headers, description ->
                var request = webTestClient.get().uri("${applicationProperties.apiBasePath}/http-headers")
                headers.forEach { (name, value) ->
                    request = request.header(name, value)
                }
                request
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectHeader()
                    .contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.headers")
                    .exists()
                    .jsonPath("$.headers")
                    .isArray()
            }
        }

        test("Response and Header data classes should have proper structure") {
            val header = HttpHeadersApiHandler.Response.Header("Test-Header", "test-value")
            header.name shouldBe "Test-Header"
            header.value shouldBe "test-value"

            val response = HttpHeadersApiHandler.Response(listOf(header))
            response.headers shouldNotBe null
            response.headers.size shouldBe 1
            response.headers[0].name shouldBe "Test-Header"
        }
    })
