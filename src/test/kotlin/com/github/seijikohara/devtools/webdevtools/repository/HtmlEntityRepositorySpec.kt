package com.github.seijikohara.devtools.webdevtools.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest

@SpringBootTest
class HtmlEntityRepositorySpec(
    private val htmlEntityRepository: HtmlEntityRepository,
) : FunSpec({

        test("htmlEntityRepository should be properly configured") {
            htmlEntityRepository shouldNotBe null
        }

        test("findByNameContaining should return entities matching the name pattern") {
            val pageable = PageRequest.of(0, 10)
            val entities =
                htmlEntityRepository
                    .findByNameContaining("amp", pageable)
                    .toList()

            entities shouldNotBe null
            entities.should { list ->
                list.any { it.name.contains("amp") }
            }
        }

        test("countByNameContaining should return count of entities matching the name pattern") {
            val count = htmlEntityRepository.countByNameContaining("amp")

            count shouldNotBe null
            count shouldBeGreaterThan 0L
        }

        test("findAll should return all entities") {
            val entities =
                htmlEntityRepository
                    .findAll()
                    .toList()

            entities shouldNotBe null
            entities.size shouldBeGreaterThan 0
        }

        test("findById should return entity by ID") {
            val firstEntity =
                htmlEntityRepository
                    .findAll()
                    .firstOrNull()

            if (firstEntity != null && firstEntity.id != null) {
                val foundEntity = htmlEntityRepository.findById(firstEntity.id!!)

                foundEntity shouldNotBe null
                foundEntity!!.id shouldNotBe null
            }
        }

        test("HtmlEntity data class should have proper structure") {
            val entity =
                HtmlEntity(
                    id = 1L,
                    name = "amp",
                    code = 38L,
                    code2 = null,
                    standard = "HTML",
                    dtd = "HTML",
                    description = "ampersand",
                )

            entity.id shouldNotBe null
            entity.name shouldNotBe null
            entity.code shouldNotBe null
        }
    })
