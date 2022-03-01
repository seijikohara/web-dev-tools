package net.relaxism.devtools.webdevtools.handler

import io.kotest.core.spec.style.StringSpec
import net.relaxism.devtools.webdevtools.config.ApplicationProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HttpHeadersApiHandlerSpec(
    @Autowired private val applicationProperties: ApplicationProperties,
    @Autowired private val webTestClient: WebTestClient,
    @LocalServerPort private val localServerPort: Int
) : StringSpec() {

    init {
        "get response" {
            val customHeaderName = "Custom-Header"
            val customHeaderValue1 = "value1"
            val customHeaderValue2 = "value2"
            webTestClient.get()
                .uri("${applicationProperties.apiBasePath}/http-headers")
                .header(customHeaderName, customHeaderValue1, customHeaderValue2)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.headers[2].value").isEqualTo("localhost:${localServerPort}")
                .jsonPath("$.headers[4].name").isEqualTo(customHeaderName)
                .jsonPath("$.headers[4].value").isEqualTo(customHeaderValue1)
                .jsonPath("$.headers[5].name").isEqualTo(customHeaderName)
                .jsonPath("$.headers[5].value").isEqualTo(customHeaderValue2)
        }
    }

}
