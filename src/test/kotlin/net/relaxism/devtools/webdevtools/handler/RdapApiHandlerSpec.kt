package net.relaxism.devtools.webdevtools.handler

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import net.relaxism.devtools.webdevtools.config.ApplicationProperties
import net.relaxism.devtools.webdevtools.service.ExternalJsonApiService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.net.URI

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RdapApiHandlerSpec(
    @MockkBean private val externalJsonApiService: ExternalJsonApiService,
    @Autowired private val applicationProperties: ApplicationProperties,
    @Autowired private val webTestClient: WebTestClient
) : StringSpec() {

    init {
        val rdapProperties = applicationProperties.network.rdap
        val ipAddress = "192.0.2.1"

        "get response" {
            every { externalJsonApiService.get(URI.create("${rdapProperties.uri}/${ipAddress}")) } returns Mono.just(mapOf<String?, Any?>("key1" to "value1"))

            webTestClient.get()
                .uri("/api/rdap/${ipAddress}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{\"rdap\":{\"key1\":\"value1\"}}")
        }

    }

}
