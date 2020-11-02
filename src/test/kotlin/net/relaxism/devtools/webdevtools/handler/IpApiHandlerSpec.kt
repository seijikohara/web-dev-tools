package net.relaxism.devtools.webdevtools.handler

import io.kotest.core.spec.style.StringSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IpApiHandlerSpec(
    @Autowired private val webTestClient: WebTestClient
) : StringSpec() {

    init {

        "get response" {
            val ipAddress = "192.0.2.1"

            webTestClient.get()
                .uri("/api/ip")
                .header("X-Forwarded-For", ipAddress)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{\"ipAddress\":\"${ipAddress}\",\"hostName\":\"localhost\"}")
        }

    }

}
