package io.github.seijikohara.devtools.domain.common.extensions

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ResultExtensionsSpec :
    FunSpec({

        context("List<Result<T>>.sequence()") {
            test("should transform list of successful results into successful result of list") {
                val results = listOf(Result.success(1), Result.success(2), Result.success(3))

                val sequenced = results.sequence()

                sequenced.isSuccess shouldBe true
                sequenced.getOrNull() shouldBe listOf(1, 2, 3)
            }

            test("should return first failure when any result fails") {
                val exception = RuntimeException("Error at index 1")
                val results =
                    listOf(
                        Result.success(1),
                        Result.failure<Int>(exception),
                        Result.success(3),
                    )

                val sequenced = results.sequence()

                sequenced.isFailure shouldBe true
                sequenced.exceptionOrNull() shouldBe exception
            }

            test("should return empty list for empty input") {
                val results = emptyList<Result<Int>>()

                val sequenced = results.sequence()

                sequenced.isSuccess shouldBe true
                sequenced.getOrNull() shouldBe emptyList()
            }

            test("should preserve order of values") {
                val results =
                    listOf(
                        Result.success("first"),
                        Result.success("second"),
                        Result.success("third"),
                    )

                val sequenced = results.sequence()

                sequenced.isSuccess shouldBe true
                sequenced.getOrNull() shouldBe listOf("first", "second", "third")
            }

            test("should handle single element list") {
                val results = listOf(Result.success(42))

                val sequenced = results.sequence()

                sequenced.isSuccess shouldBe true
                sequenced.getOrNull() shouldBe listOf(42)
            }

            test("should return first failure among multiple failures") {
                val firstException = RuntimeException("First error")
                val secondException = RuntimeException("Second error")
                val results =
                    listOf(
                        Result.failure<Int>(firstException),
                        Result.failure<Int>(secondException),
                    )

                val sequenced = results.sequence()

                sequenced.isFailure shouldBe true
                sequenced.exceptionOrNull() shouldBe firstException
            }
        }

        context("Result<T>.flatMap()") {
            test("should apply transform to successful result") {
                val result = Result.success(5)

                val transformed = result.flatMap { value -> Result.success(value * 2) }

                transformed.isSuccess shouldBe true
                transformed.getOrNull() shouldBe 10
            }

            test("should propagate failure without applying transform") {
                val exception = RuntimeException("Original error")
                val result = Result.failure<Int>(exception)

                val transformed = result.flatMap { value -> Result.success(value * 2) }

                transformed.isFailure shouldBe true
                transformed.exceptionOrNull() shouldBe exception
            }

            test("should handle transform that returns failure") {
                val result = Result.success(5)
                val transformException = RuntimeException("Transform error")

                val transformed = result.flatMap { Result.failure<Int>(transformException) }

                transformed.isFailure shouldBe true
                transformed.exceptionOrNull() shouldBe transformException
            }

            test("should chain multiple flatMap operations") {
                val result = Result.success(2)

                val transformed =
                    result
                        .flatMap { value -> Result.success(value * 2) }
                        .flatMap { value -> Result.success(value + 3) }
                        .flatMap { value -> Result.success(value.toString()) }

                transformed.isSuccess shouldBe true
                transformed.getOrNull() shouldBe "7"
            }

            test("should short-circuit on first failure in chain") {
                val result = Result.success(2)
                val exception = RuntimeException("Middle error")

                val transformed =
                    result
                        .flatMap { value -> Result.success(value * 2) }
                        .flatMap<Int, Int> { Result.failure(exception) }
                        .flatMap { value -> Result.success(value + 100) }

                transformed.isFailure shouldBe true
                transformed.exceptionOrNull() shouldBe exception
            }
        }
    })
