---
paths: src/test/**/*.kt
---

# Unit Testing Guidelines (Kotest + MockK)

## File Location

Tests are located in `src/test/kotlin/` mirroring the source structure:

```
src/
├── main/kotlin/.../application/usecase/SearchHtmlEntitiesUseCase.kt
└── test/kotlin/.../application/usecase/SearchHtmlEntitiesUseCaseSpec.kt
```

## Test Class Naming

All test classes must end with `Spec`:

```kotlin
class SearchHtmlEntitiesUseCaseSpec : FunSpec({ ... })
class PaginationSpec : FunSpec({ ... })
class CleanArchitectureSpec : FunSpec({ ... })
```

## Test Structure (FunSpec)

Use Kotest's FunSpec style with `context` and `test`:

```kotlin
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class SearchHtmlEntitiesUseCaseSpec :
    FunSpec({
        context("SearchHtmlEntitiesUseCase execution") {
            test("should return entities when search succeeds") {
                val mockRepository = mockk<HtmlEntityRepository>()
                // Arrange
                coEvery { mockRepository.searchByName(any(), any()) } returns mockResult

                // Act
                val useCase = searchHtmlEntitiesUseCase(mockRepository)
                val result = useCase(request)

                // Assert
                result.isSuccess shouldBe true
                result.getOrNull()?.entities?.size shouldBe expectedSize
            }
        }

        context("SearchHtmlEntitiesUseCase with invalid parameters") {
            test("should return failure when page size is invalid") {
                // ...
            }
        }
    })
```

## Data-Driven Tests

Use `withData` for parameterized tests:

```kotlin
import io.kotest.datatest.withData

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
    ) { (page, size, expectedOffset, _) ->
        val mockRepository = mockk<HtmlEntityRepository>()
        // Test implementation
    }
}
```

## Mocking with MockK

### Coroutine Mocking

Use `coEvery` and `coVerify` for suspend functions:

```kotlin
val mockRepository = mockk<HtmlEntityRepository>()

// Setup mock
coEvery { mockRepository.searchByName("test", pagination) } returns mockResult

// Execute
val result = useCase(request)

// Verify
coVerify(exactly = 1) { mockRepository.searchByName("test", pagination) }
```

### Argument Matchers

```kotlin
// Any argument
coVerify { mockRepository.searchByName(any(), any()) }

// Specific matcher
coVerify {
    mockRepository.searchByName(
        "test",
        match { it.offset == expectedOffset && it.pageSize.value == size },
    )
}

// No invocation
coVerify(exactly = 0) { mockRepository.searchByName(any(), any()) }
```

### Error Mocking

```kotlin
coEvery { mockRepository.searchByName("test", pagination) } throws RuntimeException("Database error")
```

## Assertions

### Basic Assertions

```kotlin
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

result.isSuccess shouldBe true
result.getOrNull() shouldNotBe null
response.entities.size shouldBe 3
response.totalCount shouldBe 100L
```

### Collection Assertions

```kotlin
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize

response.entities shouldHaveSize 3
response.entities shouldContain expectedEntity
```

### Exception Assertions

```kotlin
import io.kotest.assertions.throwables.shouldThrow

shouldThrow<IllegalArgumentException> {
    PageSize.of(-1).getOrThrow()
}
```

### Result Type Assertions

```kotlin
result.isSuccess shouldBe true
result.isFailure shouldBe true
result.exceptionOrNull() shouldNotBe null
result.getOrNull()?.field shouldBe expectedValue
```

## Test Organization

### Order Within Context

1. **Happy path tests** - Normal successful operations
2. **Edge cases** - Boundary conditions
3. **Error cases** - Expected failures
4. **Default values** - Optional parameters

### Example Structure

```kotlin
class SomeUseCaseSpec :
    FunSpec({
        context("SomeUseCase execution") {
            // Happy path tests
            test("should succeed with valid input") { ... }
        }

        context("SomeUseCase pagination") {
            // Parameterized tests for pagination
            withData(...) { ... }
        }

        context("SomeUseCase with invalid parameters") {
            // Error cases
            withData(...) { ... }
        }

        context("SomeUseCase with repository errors") {
            test("should propagate repository errors") { ... }
            test("should handle empty results") { ... }
        }

        context("SomeUseCase with default parameters") {
            test("should use default values") { ... }
        }
    })
```

## Running Tests

```bash
./gradlew test              # Run all unit tests
./gradlew test --tests "*UseCaseSpec"  # Run specific tests
./gradlew test --info       # Run with detailed output
```
