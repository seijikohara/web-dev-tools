package net.relaxism.devtools.webdevtools.handler

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import net.relaxism.devtools.webdevtools.config.ApplicationProperties
import net.relaxism.devtools.webdevtools.repository.HtmlEntity
import net.relaxism.devtools.webdevtools.service.HtmlEntityService
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HtmlEntitiesApiHandlerSpec(
    private val webTestClient: WebTestClient,
    private val applicationProperties: ApplicationProperties,
    @MockkBean private val mockHtmlEntityService: HtmlEntityService,
) : FunSpec({

        test("getHtmlEntities should handle various query parameters") {
            val mockEntities =
                listOf(
                    HtmlEntity(1L, "amp", 38L, null, "HTML", "HTML", "ampersand"),
                    HtmlEntity(2L, "quot", 34L, null, "HTML", "HTML", "quotation mark"),
                )

            forAll(
                row(
                    "${applicationProperties.apiBasePath}/html-entities",
                    "",
                    PageRequest.of(0, 50, Sort.by(Sort.Order.asc("id"))),
                    "basic request",
                ),
                row(
                    "${applicationProperties.apiBasePath}/html-entities?page=0&size=10",
                    "",
                    PageRequest.of(0, 10, Sort.by(Sort.Order.asc("id"))),
                    "pagination parameters",
                ),
                row(
                    "${applicationProperties.apiBasePath}/html-entities?name=amp",
                    "amp",
                    PageRequest.of(0, 50, Sort.by(Sort.Order.asc("id"))),
                    "name filtering",
                ),
                row(
                    "${applicationProperties.apiBasePath}/html-entities?name=quot&page=0&size=5",
                    "quot",
                    PageRequest.of(0, 5, Sort.by(Sort.Order.asc("id"))),
                    "combined parameters",
                ),
            ) { uri, nameFilter, expectedPageable, description ->
                val mockPage = PageImpl(mockEntities, expectedPageable, mockEntities.size.toLong())
                coEvery { mockHtmlEntityService.findByNameContaining(nameFilter, expectedPageable) } returns mockPage

                webTestClient
                    .get()
                    .uri(uri)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectHeader()
                    .contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.content")
                    .exists()
                    .jsonPath("$.pageable")
                    .exists()
                    .jsonPath("$.totalElements")
                    .exists()
            }
        }

        test("Entity data class should have proper structure and entityReference property") {
            val entity =
                HtmlEntitiesApiHandler.Entity(
                    name = "amp",
                    code = 38,
                    code2 = null,
                    standard = "HTML",
                    dtd = "HTML",
                    description = "ampersand",
                )

            entity.name shouldBe "amp"
            entity.code shouldBe 38
            entity.entityReference shouldBe "&#38;"
        }

        test("Entity with code2 should generate proper entityReference") {
            val entity =
                HtmlEntitiesApiHandler.Entity(
                    name = "test",
                    code = 123,
                    code2 = 456,
                    standard = "HTML",
                    dtd = "HTML",
                    description = "test entity",
                )

            entity.entityReference shouldBe "&#123;&#456;"
        }
    })
