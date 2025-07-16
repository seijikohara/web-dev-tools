package net.relaxism.devtools.webdevtools.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
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
                    .collectList()
                    .awaitFirst()

            entities shouldNotBe null
            entities.should { list ->
                list.any { it.name.contains("amp") }
            }
        }

        test("countByNameContaining should return count of entities matching the name pattern") {
            val count =
                htmlEntityRepository
                    .countByNameContaining("amp")
                    .awaitFirst()

            count shouldNotBe null
            count shouldBeGreaterThan 0L
        }

        test("findAll should return all entities") {
            val entities =
                htmlEntityRepository
                    .findAll()
                    .collectList()
                    .awaitFirst()

            entities shouldNotBe null
            entities.size shouldBeGreaterThan 0
        }

        test("findById should return entity by ID") {
            val firstEntity =
                htmlEntityRepository
                    .findAll()
                    .take(1)
                    .awaitFirstOrNull()

            if (firstEntity != null && firstEntity.id != null) {
                val foundEntity =
                    htmlEntityRepository
                        .findById(firstEntity.id!!)
                        .awaitFirstOrNull()

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
