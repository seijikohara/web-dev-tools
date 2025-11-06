package io.github.seijikohara.devtools.domain.common.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class PageSizeSpec :
    FunSpec({

        context("PageSize.of() with valid values") {
            withData(
                1 to "minimum value",
                10 to "small value",
                500 to "middle value",
                1000 to "maximum value",
            ) { (value, _) ->
                val result = PageSize.of(value)
                result.isSuccess shouldBe true
                result.getOrNull()?.value shouldBe value
            }
        }

        context("PageSize.of() with invalid values") {
            withData(
                0 to "zero",
                -1 to "negative value",
                -100 to "large negative value",
                1001 to "just above maximum",
                10000 to "much larger than maximum",
            ) { (value, _) ->
                val result = PageSize.of(value)
                result.isFailure shouldBe true
                result.exceptionOrNull()?.message shouldContain "Page size must be between"
            }
        }

        context("PageSize error messages") {
            test("should include the invalid value in error message") {
                val result = PageSize.of(2000)
                result.isFailure shouldBe true
                result.exceptionOrNull()?.message shouldContain "2000"
            }

            test("should specify valid range in error message") {
                val result = PageSize.of(0)
                result.isFailure shouldBe true
                val message = result.exceptionOrNull()?.message ?: ""
                message shouldContain "1"
                message shouldContain "1000"
            }
        }
    })
