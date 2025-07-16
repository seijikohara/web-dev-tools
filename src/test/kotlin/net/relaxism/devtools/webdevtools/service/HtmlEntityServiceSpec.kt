package net.relaxism.devtools.webdevtools.service

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import kotlinx.coroutines.flow.toList
import net.relaxism.devtools.webdevtools.repository.HtmlEntity
import net.relaxism.devtools.webdevtools.repository.HtmlEntityRepository
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@SpringBootTest
class HtmlEntityServiceSpec(
    private val htmlEntityService: HtmlEntityService,
    @MockkBean private val mockHtmlEntityRepository: HtmlEntityRepository,
) : FunSpec({

        test("htmlEntityService should be properly configured") {
            htmlEntityService shouldNotBe null
        }

        test("findAll should return flow of all entities") {
            val mockEntities =
                listOf(
                    HtmlEntity(1L, "amp", 38L, null, "HTML", "HTML", "ampersand"),
                    HtmlEntity(2L, "quot", 34L, null, "HTML", "HTML", "quotation mark"),
                )
            every { mockHtmlEntityRepository.findAll() } returns Flux.fromIterable(mockEntities)

            val entities = htmlEntityService.findAll().toList()

            entities shouldNotBe null
            entities.size shouldBe 2
            entities[0].name shouldBe "amp"
        }

        test("findByNameContaining should return paginated entities matching the name pattern") {
            val pageable = PageRequest.of(0, 10)
            val mockEntities =
                listOf(
                    HtmlEntity(1L, "amp", 38L, null, "HTML", "HTML", "ampersand"),
                )
            every { mockHtmlEntityRepository.countByNameContaining("amp") } returns Mono.just(1L)
            every { mockHtmlEntityRepository.findByNameContaining("amp", pageable) } returns Flux.fromIterable(mockEntities)

            val page = htmlEntityService.findByNameContaining("amp", pageable)

            page shouldNotBe null
            page.content shouldNotBe null
            page.totalElements shouldBe 1L
            page.content.should { list ->
                list.any { it.name.contains("amp") }
            }
        }

        test("findByNameContaining should handle empty search term") {
            val pageable = PageRequest.of(0, 10)
            val mockEntities =
                listOf(
                    HtmlEntity(1L, "amp", 38L, null, "HTML", "HTML", "ampersand"),
                    HtmlEntity(2L, "quot", 34L, null, "HTML", "HTML", "quotation mark"),
                )
            every { mockHtmlEntityRepository.countByNameContaining("") } returns Mono.just(2L)
            every { mockHtmlEntityRepository.findByNameContaining("", pageable) } returns Flux.fromIterable(mockEntities)

            val page = htmlEntityService.findByNameContaining("", pageable)

            page shouldNotBe null
            page.content shouldNotBe null
            page.totalElements shouldBe 2L
        }

        test("findByNameContaining should respect pagination parameters") {
            val pageable = PageRequest.of(0, 5)
            val mockEntities =
                listOf(
                    HtmlEntity(1L, "amp", 38L, null, "HTML", "HTML", "ampersand"),
                )
            every { mockHtmlEntityRepository.countByNameContaining("") } returns Mono.just(1L)
            every { mockHtmlEntityRepository.findByNameContaining("", pageable) } returns Flux.fromIterable(mockEntities)

            val page = htmlEntityService.findByNameContaining("", pageable)

            page shouldNotBe null
            page.content.size shouldBe 1
            page.size shouldBe 5
            page.number shouldBe 0
        }

        test("findByNameContaining should handle case-insensitive search") {
            val pageable = PageRequest.of(0, 10)
            val mockEntities =
                listOf(
                    HtmlEntity(1L, "amp", 38L, null, "HTML", "HTML", "ampersand"),
                )
            every { mockHtmlEntityRepository.countByNameContaining("amp") } returns Mono.just(1L)
            every { mockHtmlEntityRepository.findByNameContaining("amp", pageable) } returns Flux.fromIterable(mockEntities)
            every { mockHtmlEntityRepository.countByNameContaining("AMP") } returns Mono.just(1L)
            every { mockHtmlEntityRepository.findByNameContaining("AMP", pageable) } returns Flux.fromIterable(mockEntities)

            val lowerCasePage = htmlEntityService.findByNameContaining("amp", pageable)
            val upperCasePage = htmlEntityService.findByNameContaining("AMP", pageable)

            lowerCasePage shouldNotBe null
            upperCasePage shouldNotBe null
            lowerCasePage.totalElements shouldBe 1L
            upperCasePage.totalElements shouldBe 1L
        }
    })
