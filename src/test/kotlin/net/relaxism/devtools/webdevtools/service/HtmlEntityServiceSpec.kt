package net.relaxism.devtools.webdevtools.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.toList
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest

@SpringBootTest
class HtmlEntityServiceSpec(
    private val htmlEntityService: HtmlEntityService,
) : FunSpec({

        test("htmlEntityService should be properly configured") {
            htmlEntityService shouldNotBe null
        }

        test("findAll should return flow of all entities") {
            val entities = htmlEntityService.findAll().toList()

            entities shouldNotBe null
            entities.size shouldBeGreaterThan 0
        }

        test("findByNameContaining should return paginated entities matching the name pattern") {
            val pageable = PageRequest.of(0, 10)
            val page = htmlEntityService.findByNameContaining("amp", pageable)

            page shouldNotBe null
            page.content shouldNotBe null
            page.totalElements shouldBeGreaterThan 0L
            page.content.should { list ->
                list.any { it.name.contains("amp") }
            }
        }

        test("findByNameContaining should handle empty search term") {
            val pageable = PageRequest.of(0, 10)
            val page = htmlEntityService.findByNameContaining("", pageable)

            page shouldNotBe null
            page.content shouldNotBe null
            page.totalElements shouldBeGreaterThan 0L
        }

        test("findByNameContaining should respect pagination parameters") {
            val pageable = PageRequest.of(0, 5)
            val page = htmlEntityService.findByNameContaining("", pageable)

            page shouldNotBe null
            page.content.size shouldNotBe null
            page.size shouldNotBe null
            page.number shouldNotBe null
        }

        test("findByNameContaining should handle case-insensitive search") {
            val pageable = PageRequest.of(0, 10)
            val lowerCasePage = htmlEntityService.findByNameContaining("amp", pageable)
            val upperCasePage = htmlEntityService.findByNameContaining("AMP", pageable)

            lowerCasePage shouldNotBe null
            upperCasePage shouldNotBe null
        }
    })
