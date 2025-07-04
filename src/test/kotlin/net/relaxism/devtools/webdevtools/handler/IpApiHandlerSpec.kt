package net.relaxism.devtools.webdevtools.handler

import io.kotest.core.spec.style.StringSpec
import net.relaxism.devtools.webdevtools.config.ApplicationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IpApiHandlerSpec(
    private val applicationProperties: ApplicationProperties,
    private val webTestClient: WebTestClient,
) : StringSpec() {
    init {
        "get response" {
            val ipAddress = "192.0.2.1"

            webTestClient
                .get()
                .uri("${applicationProperties.apiBasePath}/ip")
                .header("X-Forwarded-For", ipAddress)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{\"ipAddress\":\"${ipAddress}\",\"hostName\":\"localhost\"}")
        }
    }
}
