package net.relaxism.devtools.webdevtools.handler

import io.kotest.core.spec.style.StringSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IndexHandlerSpec(
    @Autowired private val webTestClient: WebTestClient
) : StringSpec() {

    init {

        "get response" {
            webTestClient.get()
                .uri("/")
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.TEXT_HTML)
        }

    }

}
