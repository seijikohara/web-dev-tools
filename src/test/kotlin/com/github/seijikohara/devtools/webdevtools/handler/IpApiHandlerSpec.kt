package com.github.seijikohara.devtools.webdevtools.handler

import com.github.seijikohara.devtools.webdevtools.config.ApplicationProperties
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IpApiHandlerSpec(
    private val webTestClient: WebTestClient,
    private val applicationProperties: ApplicationProperties,
) : FunSpec({

        test("getIp should handle various header scenarios") {
            forAll(
                row(mapOf(), "no headers"),
                row(mapOf("X-Forwarded-For" to "203.0.113.1, 198.51.100.1"), "X-Forwarded-For with multiple IPs"),
                row(mapOf("X-Real-IP" to "203.0.113.2"), "X-Real-IP header"),
                row(mapOf("X-Forwarded-For" to "203.0.113.3,198.51.100.2"), "X-Forwarded-For with comma separation"),
            ) { headers, description ->
                var request = webTestClient.get().uri("${applicationProperties.apiBasePath}/ip")
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
                    .jsonPath("$.ipAddress")
                    .exists()
                    .jsonPath("$.hostName")
                    .exists()
            }
        }

        test("Response data class should have proper structure") {
            val response = IpApiHandler.Response(ipAddress = "192.168.1.1", hostName = "localhost")
            response.ipAddress shouldBe "192.168.1.1"
            response.hostName shouldBe "localhost"
        }
    })
