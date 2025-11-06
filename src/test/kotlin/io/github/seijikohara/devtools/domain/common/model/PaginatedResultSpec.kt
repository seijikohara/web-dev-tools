package io.github.seijikohara.devtools.domain.common.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class PaginatedResultSpec :
    FunSpec({

        context("PaginatedResult initialization validation") {
            test("should fail when totalCount is negative") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        PaginatedResult(
                            totalCount = -1,
                            items = emptyList<String>(),
                            pagination = Pagination(0, PageSize.of(10).getOrThrow()),
                        )
                    }
                exception.message shouldContain "Total count must be non-negative"
            }

            test("should succeed when totalCount is zero") {
                val result =
                    PaginatedResult(
                        totalCount = 0,
                        items = emptyList<String>(),
                        pagination = Pagination(0, PageSize.of(10).getOrThrow()),
                    )
                result.totalCount shouldBe 0
            }

            test("should succeed when totalCount is positive") {
                val result =
                    PaginatedResult(
                        totalCount = 100,
                        items = List(10) { "item$it" },
                        pagination = Pagination(0, PageSize.of(10).getOrThrow()),
                    )
                result.totalCount shouldBe 100
            }
        }

        context("PaginatedResult.hasMore property") {
            data class HasMoreCase(
                val totalCount: Long,
                val offset: Int,
                val itemsSize: Int,
                val pageSize: Int,
                val expectedHasMore: Boolean,
                val description: String,
            )

            withData(
                HasMoreCase(100, 0, 10, 10, true, "first page with more pages"),
                HasMoreCase(100, 90, 10, 10, false, "last page with exact fit"),
                HasMoreCase(95, 90, 5, 10, false, "last page with partial items"),
                HasMoreCase(10, 0, 10, 10, false, "single page with exact fit"),
                HasMoreCase(5, 0, 5, 10, false, "single page with fewer items"),
                HasMoreCase(0, 0, 0, 10, false, "empty result"),
                HasMoreCase(100, 50, 10, 10, true, "middle page"),
            ) { (totalCount, offset, itemsSize, pageSize, expectedHasMore, _) ->
                val result =
                    PaginatedResult(
                        totalCount = totalCount,
                        items = List(itemsSize) { "item$it" },
                        pagination = Pagination(offset, PageSize.of(pageSize).getOrThrow()),
                    )

                result.hasMore shouldBe expectedHasMore
            }
        }

        context("PaginatedResult.totalPages property") {
            data class TotalPagesCase(
                val totalCount: Long,
                val pageSize: Int,
                val expectedTotalPages: Long,
                val description: String,
            )

            withData(
                TotalPagesCase(0, 10, 0, "empty result"),
                TotalPagesCase(10, 10, 1, "exact single page"),
                TotalPagesCase(15, 10, 2, "two pages with partial"),
                TotalPagesCase(100, 10, 10, "exact 10 pages"),
                TotalPagesCase(101, 10, 11, "11 pages with one extra item"),
                TotalPagesCase(1, 10, 1, "single item"),
                TotalPagesCase(99, 10, 10, "partial last page"),
            ) { (totalCount, pageSize, expectedTotalPages, _) ->
                val result =
                    PaginatedResult(
                        totalCount = totalCount,
                        items = emptyList<String>(),
                        pagination = Pagination(0, PageSize.of(pageSize).getOrThrow()),
                    )

                result.totalPages shouldBe expectedTotalPages
            }
        }

        context("PaginatedResult.map() transformation") {
            test("should transform items using map function") {
                val result =
                    PaginatedResult(
                        totalCount = 3,
                        items = listOf(1, 2, 3),
                        pagination = Pagination(0, PageSize.of(10).getOrThrow()),
                    )

                val mapped = result.map { it * 2 }

                mapped.items shouldBe listOf(2, 4, 6)
                mapped.totalCount shouldBe 3
                mapped.pagination shouldBe result.pagination
            }

            test("should transform items to different type") {
                data class Item(
                    val id: Int,
                    val name: String,
                )

                val result =
                    PaginatedResult(
                        totalCount = 2,
                        items = listOf(Item(1, "one"), Item(2, "two")),
                        pagination = Pagination(0, PageSize.of(10).getOrThrow()),
                    )

                val mapped = result.map { it.name }

                mapped.items shouldBe listOf("one", "two")
                mapped.totalCount shouldBe 2
            }

            test("should handle empty items") {
                val result =
                    PaginatedResult(
                        totalCount = 0,
                        items = emptyList<Int>(),
                        pagination = Pagination(0, PageSize.of(10).getOrThrow()),
                    )

                val mapped = result.map { it.toString() }

                mapped.items shouldBe emptyList()
                mapped.totalCount shouldBe 0
            }

            test("should preserve pagination metadata") {
                val originalPagination = Pagination(50, PageSize.of(25).getOrThrow())
                val result =
                    PaginatedResult(
                        totalCount = 100,
                        items = List(25) { it },
                        pagination = originalPagination,
                    )

                val mapped = result.map { "item$it" }

                mapped.pagination shouldBe originalPagination
                mapped.totalCount shouldBe 100
            }
        }

        context("PaginatedResult data class properties") {
            test("should correctly store all properties") {
                val items = listOf("a", "b", "c")
                val pagination = Pagination(20, PageSize.of(10).getOrThrow())

                val result =
                    PaginatedResult(
                        totalCount = 50,
                        items = items,
                        pagination = pagination,
                    )

                result.totalCount shouldBe 50
                result.items shouldBe items
                result.pagination shouldBe pagination
            }
        }
    })
