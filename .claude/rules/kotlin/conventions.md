---
paths: src/**/*.kt
---

# Kotlin Conventions

## Fundamental Principles

### Reference Compliance

- Always follow the latest Kotlin official documentation
- Consult the official reference before implementation
- Check the project's dependency versions (build.gradle.kts, libs.versions.toml) for compatible features
- Avoid deprecated patterns and legacy syntax
- Prefer idiomatic Kotlin over Java-style patterns

### Pure Function Orientation

Prioritize pure functions wherever possible:

```kotlin
// ✅ Pure function: no side effects, deterministic output
fun calculateTotal(items: List<Item>): Int =
    items.sumOf { it.price * it.quantity }

// ✅ Separate pure logic from stateful wrappers
fun formatCurrency(value: Int, locale: Locale): String =
    NumberFormat.getCurrencyInstance(locale).format(value)

// ✅ Use expression body for pure transformations
fun HtmlEntity.toDto(): HtmlEntityDto =
    HtmlEntityDto(
        id = id,
        name = name,
        reference = entityReference(),
    )
```

### Modern Syntax Enforcement

Always use the latest Kotlin syntax features:

```kotlin
// ✅ Use data class for value objects
data class Pagination(val offset: Int, val pageSize: PageSize)

// ✅ Use fun interface for SAM (Single Abstract Method)
fun interface UseCase<I, O> {
    suspend operator fun invoke(input: I): O
}

// ✅ Use trailing lambda syntax
list.filter { it.isActive }.map { it.value }

// ✅ Use scope functions appropriately
result.let { response -> Response(response) }
config.apply { timeout = 5000 }

// ✅ Use when expression (not statement)
val result = when (status) {
    Status.SUCCESS -> handleSuccess()
    Status.ERROR -> handleError()
}

// ✅ Use destructuring declarations
val (count, entities) = Pair(countDeferred.await(), entitiesDeferred.await())

// ✅ Use Elvis operator
val name = user?.name ?: "Unknown"

// ✅ Use safe call chains
val city = user?.address?.city

// ✅ Use in range operator
require(value in MIN..MAX) { "Value out of range" }
```

## Expression-Bodied Functions (Mandatory)

Always prefer expression syntax over block syntax:

```kotlin
// ❌ Avoid block body for single expressions
fun calculate(x: Int): Int {
    return x * 2
}

// ✅ Use expression body
fun calculate(x: Int): Int = x * 2

// ❌ Avoid
fun getMessage(code: Int): String {
    return when (code) {
        200 -> "OK"
        404 -> "Not Found"
        else -> "Unknown"
    }
}

// ✅ Preferred
fun getMessage(code: Int): String = when (code) {
    200 -> "OK"
    404 -> "Not Found"
    else -> "Unknown"
}
```

## Method Chaining (Mandatory)

Use method chaining for data transformation. Never use `for` or `while` for transformations:

```kotlin
// ❌ Avoid imperative loops for data transformation
val result = mutableListOf<Int>()
for (item in items) {
    if (item.active) {
        result.add(item.value * 2)
    }
}

// ✅ Use method chaining
val result = items
    .filter { it.active }
    .map { it.value * 2 }

// ❌ Avoid
var total = 0
for (item in items) {
    if (item.active) {
        total += item.value
    }
}

// ✅ Use fold/reduce/sumOf
val total = items
    .filter { it.active }
    .sumOf { it.value }

// ✅ Use sequence for large collections
val result = items.asSequence()
    .filter { it.active }
    .map { it.value * 2 }
    .toList()

// ✅ forEach is allowed for side effects only (no return value needed)
items.forEach { it.reset() }
```

## String Handling (Mandatory)

### String Interpolation

Always use string templates for string construction. Never use concatenation:

```kotlin
// ❌ Avoid string concatenation
val message = "Hello, " + name + "!"
val path = baseUrl + "/api/" + endpoint

// ✅ Use string templates
val message = "Hello, $name!"
val path = "$baseUrl/api/$endpoint"

// ✅ Use ${} for expressions
val summary = "Total: ${items.size} items, ${calculateTotal(items)} yen"
```

### Multiline Strings (Mandatory)

Never use `\n` for line breaks. Always use raw strings with trimIndent/trimMargin:

```kotlin
// ❌ Avoid escape sequences for newlines
val text = "Line 1\nLine 2\nLine 3"
val sql = "SELECT *\nFROM users\nWHERE active = true"

// ✅ Use raw strings with trimIndent
val text = """
    Line 1
    Line 2
    Line 3
""".trimIndent()

val sql = """
    SELECT *
    FROM users
    WHERE active = true
""".trimIndent()

// ✅ Use trimMargin for custom margin prefix
val message = """
    |Dear $name,
    |
    |Thank you for your order.
    |
    |Best regards
""".trimMargin()
```

### Error Messages

Use string templates in error messages:

```kotlin
// ✅ Clear error messages with interpolation
require(offset >= 0) { "Offset must be non-negative, but got $offset" }
require(value in MIN..MAX) { "Value must be between $MIN and $MAX, but got $value" }
```

## Data Classes

Use `data class` for value objects and DTOs:

```kotlin
data class Pagination(
    val offset: Int,
    val pageSize: PageSize,
) {
    init {
        require(offset >= 0) { "Offset must be non-negative, but got $offset" }
    }

    companion object {
        fun of(offset: Int, pageSize: Int): Result<Pagination> =
            PageSize.of(pageSize).mapCatching { validPageSize ->
                Pagination(offset, validPageSize)
            }
    }
}
```

### Functional Interfaces

Use `fun interface` for use cases and single-method interfaces:

```kotlin
fun interface SearchHtmlEntitiesUseCase {
    suspend operator fun invoke(request: Request): Result<Response>

    data class Request(
        val name: String,
        val page: Int = 0,
        val size: Int = 50,
    )

    data class Response(
        val entities: List<HtmlEntity>,
        val totalCount: Long,
    )
}
```

### Factory Functions

Use factory functions to create implementations:

```kotlin
fun searchHtmlEntitiesUseCase(repository: HtmlEntityRepository): SearchHtmlEntitiesUseCase =
    SearchHtmlEntitiesUseCase { request ->
        Pagination
            .of(request.page * request.size, request.size)
            .mapCatching { pagination ->
                repository.searchByName(request.name, pagination)
            }.map { result ->
                SearchHtmlEntitiesUseCase.Response(
                    entities = result.items,
                    totalCount = result.totalCount,
                )
            }
    }
```

## Coroutines

### Suspend Functions

Use `suspend` for all async operations:

```kotlin
interface HtmlEntityRepository {
    suspend fun searchByName(name: String, pagination: Pagination): PaginatedResult<HtmlEntity>
}
```

### Structured Concurrency

Use `coroutineScope` for parallel operations:

```kotlin
override suspend fun searchByName(
    name: String,
    pagination: Pagination,
): PaginatedResult<HtmlEntity> =
    coroutineScope {
        Pair(
            async { dbRepository.countByNameContaining(name) },
            async { dbRepository.findByNameContaining(name, pageRequest).toList() },
        ).let { (count, entities) ->
            PaginatedResult(
                totalCount = count.await(),
                items = entities.await().map { it.toDomain() }.sequence().getOrThrow(),
                pagination = pagination,
            )
        }
    }
```

## Result Type

### Validation

Use `Result<T>` for validation in factory methods:

```kotlin
data class PageSize(val value: Int) {
    companion object {
        private const val MIN_VALUE = 1
        private const val MAX_VALUE = 1000

        fun of(value: Int): Result<PageSize> = runCatching {
            require(value in MIN_VALUE..MAX_VALUE) {
                "Page size must be between $MIN_VALUE and $MAX_VALUE, but got $value"
            }
            PageSize(value)
        }
    }
}
```

### Chaining

Use `.map`, `.mapCatching`, `.fold` for Result transformations:

```kotlin
// ✅ Preferred
Pagination.of(offset, size)
    .mapCatching { pagination -> repository.search(pagination) }
    .map { result -> Response(result) }
    .fold(
        onSuccess = { it },
        onFailure = { throw ResponseStatusException(HttpStatus.BAD_REQUEST, it.message) }
    )

// ❌ Avoid
try {
    val pagination = Pagination.of(offset, size).getOrThrow()
    val result = repository.search(pagination)
    Response(result)
} catch (e: IllegalArgumentException) {
    throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
}
```

## Extension Functions

### Result Extensions

Use extension functions for common patterns:

```kotlin
fun <T> List<Result<T>>.sequence(): Result<List<T>> =
    fold(Result.success(mutableListOf())) { acc, result ->
        acc.flatMap { list ->
            result.map { list.apply { add(it) } }
        }
    }
```

### Domain Conversions

Use extension functions for mapping between layers:

```kotlin
fun HtmlEntityDbEntity.toDomain(): Result<HtmlEntity> =
    EntityCode.of(code).map { entityCode ->
        HtmlEntity(
            id = id,
            name = name,
            code = entityCode,
            // ...
        )
    }
```

## Spring Integration

### Constructor Injection

Use constructor injection (primary constructor):

```kotlin
@RestController
@RequestMapping("\${application.api-base-path}")
class HtmlReferenceController(
    private val searchHtmlEntitiesUseCase: SearchHtmlEntitiesUseCase,
)
```

### Configuration Classes

Use expression-bodied bean definitions:

```kotlin
@Configuration
class RepositoryConfiguration(
    private val applicationProperties: ApplicationProperties,
) {
    @Bean
    fun htmlEntityRepository(dbRepository: HtmlEntityDbRepository): HtmlEntityRepository =
        HtmlEntityRepositoryAdapter(dbRepository)
}
```

## Reactive Streams

### Flow Usage

Use Kotlin Flow with R2DBC:

```kotlin
interface HtmlEntityDbRepository : R2dbcRepository<HtmlEntityDbEntity, Long> {
    fun findByNameContaining(name: String, pageable: Pageable): Flow<HtmlEntityDbEntity>
    suspend fun countByNameContaining(name: String): Long
}
```

### WebClient

Use WebClient with coroutines:

```kotlin
suspend fun fetch(url: String): Response =
    webClient
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono<Response>()
        .awaitSingle()
```

## Serialization

### kotlinx.serialization

Use `@Serializable` for JSON models:

```kotlin
@Serializable
data class GoogleDnsResponse(
    @SerialName("Status") val status: Int,
    @SerialName("Answer") val answer: List<Answer>?,
) {
    @Serializable
    data class Answer(
        val name: String,
        val type: Int,
        val data: String,
    )
}
```
