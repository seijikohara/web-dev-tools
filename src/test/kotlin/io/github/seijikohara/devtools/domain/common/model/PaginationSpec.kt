package io.github.seijikohara.devtools.domain.common.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class PaginationSpec :
    FunSpec({

        context("Pagination creation with valid parameters") {
            data class ValidPaginationCase(
                val offset: Int,
                val pageSize: Int,
                val expectedPageNumber: Int,
                val expectedNextOffset: Int,
                val description: String,
            )

            withData(
                ValidPaginationCase(0, 10, 0, 10, "first page"),
                ValidPaginationCase(10, 10, 1, 20, "second page"),
                ValidPaginationCase(20, 10, 2, 30, "third page"),
                ValidPaginationCase(0, 50, 0, 50, "larger page size"),
                ValidPaginationCase(100, 25, 4, 125, "offset not aligned to page size"),
            ) { (offset, pageSize, expectedPageNumber, expectedNextOffset, _) ->
                val result = Pagination.of(offset, pageSize)
                result.isSuccess shouldBe true
                val pagination = result.getOrNull()!!
                pagination.offset shouldBe offset
                pagination.pageSize.value shouldBe pageSize
                pagination.pageNumber shouldBe expectedPageNumber
                pagination.nextOffset shouldBe expectedNextOffset
            }
        }

        context("Pagination creation with invalid offset") {
            data class InvalidOffsetCase(
                val offset: Int,
                val description: String,
            )

            withData(
                InvalidOffsetCase(-1, "minus one"),
                InvalidOffsetCase(-10, "minus ten"),
                InvalidOffsetCase(-100, "large negative"),
            ) { (offset, _) ->
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Pagination(offset, PageSize.of(10).getOrThrow())
                    }
                exception.message shouldContain "Offset must be non-negative"
                exception.message shouldContain offset.toString()
            }
        }

        context("Pagination.of() with invalid pageSize") {
            data class InvalidPageSizeCase(
                val offset: Int,
                val pageSize: Int,
                val description: String,
            )

            withData(
                InvalidPageSizeCase(0, 0, "zero page size"),
                InvalidPageSizeCase(0, -1, "negative page size"),
                InvalidPageSizeCase(0, 1001, "page size exceeds maximum"),
            ) { (offset, pageSize, _) ->
                val result = Pagination.of(offset, pageSize)
                result.isFailure shouldBe true
                result.exceptionOrNull()?.message shouldContain "Page size must be between"
            }
        }

        context("pageNumber calculation") {
            data class PageNumberCase(
                val offset: Int,
                val pageSize: Int,
                val expectedPageNumber: Int,
                val description: String,
            )

            withData(
                PageNumberCase(0, 10, 0, "first page"),
                PageNumberCase(9, 10, 0, "within first page"),
                PageNumberCase(10, 10, 1, "exactly second page"),
                PageNumberCase(15, 10, 1, "within second page"),
                PageNumberCase(99, 10, 9, "tenth page"),
                PageNumberCase(100, 50, 2, "different page size"),
            ) { (offset, pageSize, expectedPageNumber, _) ->
                val pagination = Pagination.of(offset, pageSize).getOrThrow()
                pagination.pageNumber shouldBe expectedPageNumber
            }
        }

        context("nextOffset calculation") {
            data class NextOffsetCase(
                val offset: Int,
                val pageSize: Int,
                val expectedNextOffset: Int,
                val description: String,
            )

            withData(
                NextOffsetCase(0, 10, 10, "from first page"),
                NextOffsetCase(10, 10, 20, "from second page"),
                NextOffsetCase(0, 50, 50, "larger page size"),
                NextOffsetCase(17, 25, 42, "non-aligned offset"),
            ) { (offset, pageSize, expectedNextOffset, _) ->
                val pagination = Pagination.of(offset, pageSize).getOrThrow()
                pagination.nextOffset shouldBe expectedNextOffset
            }
        }
    })
