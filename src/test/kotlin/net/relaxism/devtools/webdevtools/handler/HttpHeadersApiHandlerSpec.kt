package net.relaxism.devtools.webdevtools.handler

import io.kotest.core.spec.style.StringSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HttpHeadersApiHandlerSpec(
    @Autowired private val webTestClient: WebTestClient,
    @LocalServerPort private val localServerPort: Int
) : StringSpec() {

    init {

        "get response" {
            val customHeaderName = "Custom-Header"
            val customHeaderValue1 = "value1"
            val customHeaderValue2 = "value2"
            webTestClient.get()
                .uri("/api/http-headers")
                .header(customHeaderName, customHeaderValue1, customHeaderValue2)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{\"headers\":[{\"name\":\"accept-encoding\",\"value\":\"gzip\"},{\"name\":\"user-agent\",\"value\":\"ReactorNetty/0.9.12.RELEASE\"},{\"name\":\"host\",\"value\":\"localhost:${localServerPort}\"},{\"name\":\"WebTestClient-Request-Id\",\"value\":\"1\"},{\"name\":\"${customHeaderName}\",\"value\":\"${customHeaderValue1}\"},{\"name\":\"${customHeaderName}\",\"value\":\"${customHeaderValue2}\"},{\"name\":\"Accept\",\"value\":\"application/json\"}]}")
        }

    }

}
