package io.github.seijikohara.devtools.application.usecase

import io.github.seijikohara.devtools.domain.common.model.PageSize
import io.github.seijikohara.devtools.domain.common.model.PaginatedResult
import io.github.seijikohara.devtools.domain.common.model.Pagination
import io.github.seijikohara.devtools.domain.htmlreference.model.EntityCode
import io.github.seijikohara.devtools.domain.htmlreference.model.HtmlEntity
import io.github.seijikohara.devtools.domain.htmlreference.repository.HtmlEntityRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class SearchHtmlEntitiesUseCaseSpec :
    FunSpec({

        context("SearchHtmlEntitiesUseCase execution") {
            data class SearchTestCase(
                val searchName: String,
                val page: Int,
                val size: Int,
                val entityNames: List<String>,
                val totalCount: Long,
            )

            withData(
                SearchTestCase("amp", 0, 10, listOf("amp"), 1L),
                SearchTestCase("copy", 0, 10, listOf("copy"), 1L),
                SearchTestCase("", 0, 50, listOf("amp", "lt", "gt"), 100L),
            ) { (searchName, page, size, entityNames, totalCount) ->
                val mockRepository = mockk<HtmlEntityRepository>()
                val pagination = Pagination.of(page * size, size).getOrThrow()
                val mockEntities =
                    entityNames.mapIndexed { index, name ->
                        HtmlEntity(
                            id = (index + 1).toLong(),
                            name = name,
                            code = EntityCode.of(38L + index).getOrThrow(),
                            code2 = null,
                            standard = "HTML",
                            dtd = "HTML",
                            description = "Test entity $name",
                        )
                    }
                val mockResult =
                    PaginatedResult(
                        totalCount = totalCount,
                        items = mockEntities,
                        pagination = pagination,
                    )

                coEvery { mockRepository.searchByName(searchName, pagination) } returns mockResult

                val useCase = searchHtmlEntitiesUseCase(mockRepository)
                val request =
                    SearchHtmlEntitiesUseCase.Request(
                        name = searchName,
                        page = page,
                        size = size,
                    )

                val result = useCase(request)

                result.isSuccess shouldBe true
                val response = result.getOrNull()!!
                response.entities.size shouldBe entityNames.size
                response.totalCount shouldBe totalCount
                response.page shouldBe page
                response.size shouldBe size

                coVerify(exactly = 1) { mockRepository.searchByName(searchName, pagination) }
            }
        }

        context("SearchHtmlEntitiesUseCase pagination") {
            data class PaginationTestCase(
                val page: Int,
                val size: Int,
                val expectedOffset: Int,
                val description: String,
            )

            withData(
                PaginationTestCase(0, 10, 0, "first page"),
                PaginationTestCase(1, 10, 10, "second page"),
                PaginationTestCase(2, 25, 50, "third page with different size"),
                PaginationTestCase(5, 20, 100, "sixth page"),
            ) { (page, size, expectedOffset, _) ->
                val mockRepository = mockk<HtmlEntityRepository>()
                val pagination = Pagination.of(expectedOffset, size).getOrThrow()
                val mockResult =
                    PaginatedResult(
                        totalCount = 100L,
                        items = emptyList<HtmlEntity>(),
                        pagination = pagination,
                    )

                coEvery { mockRepository.searchByName("test", pagination) } returns mockResult

                val useCase = searchHtmlEntitiesUseCase(mockRepository)
                val request =
                    SearchHtmlEntitiesUseCase.Request(
                        name = "test",
                        page = page,
                        size = size,
                    )

                val result = useCase(request)

                result.isSuccess shouldBe true

                coVerify(exactly = 1) {
                    mockRepository.searchByName(
                        "test",
                        match { it.offset == expectedOffset && it.pageSize.value == size },
                    )
                }
            }
        }

        context("SearchHtmlEntitiesUseCase with invalid parameters") {
            data class InvalidParameterCase(
                val page: Int,
                val size: Int,
                val description: String,
            )

            withData(
                InvalidParameterCase(0, 0, "zero page size"),
                InvalidParameterCase(0, -1, "negative page size"),
                InvalidParameterCase(0, 1001, "page size exceeds maximum"),
                InvalidParameterCase(-1, 10, "negative page number"),
            ) { (page, size, _) ->
                val mockRepository = mockk<HtmlEntityRepository>()
                val useCase = searchHtmlEntitiesUseCase(mockRepository)
                val request =
                    SearchHtmlEntitiesUseCase.Request(
                        name = "test",
                        page = page,
                        size = size,
                    )

                val result = useCase(request)

                result.isFailure shouldBe true
                result.exceptionOrNull() shouldNotBe null

                // Repository should not be called when validation fails
                coVerify(exactly = 0) { mockRepository.searchByName(any(), any()) }
            }
        }

        context("SearchHtmlEntitiesUseCase with repository errors") {
            test("should propagate repository errors") {
                val mockRepository = mockk<HtmlEntityRepository>()
                val repositoryError = RuntimeException("Database error")
                val pagination = Pagination.of(0, 10).getOrThrow()

                coEvery { mockRepository.searchByName("test", pagination) } throws repositoryError

                val useCase = searchHtmlEntitiesUseCase(mockRepository)
                val request =
                    SearchHtmlEntitiesUseCase.Request(
                        name = "test",
                        page = 0,
                        size = 10,
                    )

                val result = useCase(request)

                result.isFailure shouldBe true
                result.exceptionOrNull() shouldBe repositoryError

                coVerify(exactly = 1) { mockRepository.searchByName("test", pagination) }
            }

            test("should handle empty search results") {
                val mockRepository = mockk<HtmlEntityRepository>()
                val pagination = Pagination.of(0, 10).getOrThrow()
                val emptyResult =
                    PaginatedResult(
                        totalCount = 0L,
                        items = emptyList<HtmlEntity>(),
                        pagination = pagination,
                    )

                coEvery { mockRepository.searchByName("nonexistent", pagination) } returns emptyResult

                val useCase = searchHtmlEntitiesUseCase(mockRepository)
                val request =
                    SearchHtmlEntitiesUseCase.Request(
                        name = "nonexistent",
                        page = 0,
                        size = 10,
                    )

                val result = useCase(request)

                result.isSuccess shouldBe true
                val response = result.getOrNull()!!
                response.entities shouldBe emptyList()
                response.totalCount shouldBe 0L
            }
        }

        context("SearchHtmlEntitiesUseCase with default parameters") {
            test("should use default values for page and size") {
                val mockRepository = mockk<HtmlEntityRepository>()
                val defaultPagination = Pagination.of(0, 50).getOrThrow()
                val mockResult =
                    PaginatedResult(
                        totalCount = 5L,
                        items = emptyList<HtmlEntity>(),
                        pagination = defaultPagination,
                    )

                coEvery { mockRepository.searchByName("amp", defaultPagination) } returns mockResult

                val useCase = searchHtmlEntitiesUseCase(mockRepository)
                val request = SearchHtmlEntitiesUseCase.Request(name = "amp")

                val result = useCase(request)

                result.isSuccess shouldBe true
                val response = result.getOrNull()!!
                response.page shouldBe 0
                response.size shouldBe 50
            }
        }
    })
