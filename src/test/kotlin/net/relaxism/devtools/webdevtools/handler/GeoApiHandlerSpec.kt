package net.relaxism.devtools.webdevtools.handler

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import net.relaxism.devtools.webdevtools.config.ApplicationProperties
import net.relaxism.devtools.webdevtools.service.GeoIpService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GeoApiHandlerSpec(
    @MockkBean private val geoIpService: GeoIpService,
    @Autowired private val applicationProperties: ApplicationProperties,
    @Autowired private val webTestClient: WebTestClient
) : StringSpec() {

    init {
        val ipAddress = "192.0.2.1"

        "get response" {
            coEvery {
                geoIpService.getGeoFromIpAddress(ipAddress)
            } returns mapOf<String, Any?>("key1" to "value1")

            webTestClient.get()
                .uri("${applicationProperties.apiBasePath}/geo/${ipAddress}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{\"geo\":{\"key1\":\"value1\"}}")
        }
    }

}
