package net.relaxism.devtools.webdevtools.handler

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import net.relaxism.devtools.webdevtools.config.ApplicationProperties
import net.relaxism.devtools.webdevtools.repository.HtmlEntity
import net.relaxism.devtools.webdevtools.service.HtmlEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HtmlEntitiesApiHandlerSpec(
    @MockkBean private val htmlEntityService: HtmlEntityService,
    @Autowired private val applicationProperties: ApplicationProperties,
    @Autowired private val webTestClient: WebTestClient,
) : StringSpec() {

    init {

        "get response" {
            val entity1 = HtmlEntity(1, "name1", 1, 1, "standard1", "dtd1", "desc1")
            val entity2 = HtmlEntity(2, "name2", 2, 2, "standard2", "dtd2", "desc2")

            every { htmlEntityService.findAll() } returns Flux.just(entity1, entity2)

            webTestClient.get()
                .uri("${applicationProperties.apiBasePath}/html-entities")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.entities[0].name").isEqualTo(entity1.name)
                .jsonPath("$.entities[1].name").isEqualTo(entity2.name)
        }

    }

}
