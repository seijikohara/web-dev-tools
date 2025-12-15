# Code Style Guidelines

## Reference Compliance

- Always follow the latest Kotlin official documentation
- Consult the official reference before implementation
- Check the project's dependency versions (build.gradle.kts, libs.versions.toml) for compatible features
- Avoid deprecated patterns and legacy syntax
- Prefer idiomatic Kotlin over Java-style patterns

## Pure Function Orientation

Prioritize pure functions wherever possible:

- Functions should have no side effects
- Output should be deterministic based on input
- Separate pure business logic from stateful infrastructure concerns
- Use expression bodies for pure transformations

## Formatting

- Use ktlint formatting via Spotless
- Run `./gradlew spotlessApply` before committing
- Run `./gradlew spotlessCheck` to verify formatting

## Naming Conventions

| Type | Convention | Example |
|------|------------|---------|
| Packages | lowercase, dot-separated | `io.github.seijikohara.devtools.domain` |
| Classes | PascalCase | `HtmlEntityRepository`, `SearchUseCase` |
| Functions | camelCase | `searchByName`, `toDomain` |
| Properties | camelCase | `pageSize`, `totalCount` |
| Constants | UPPER_SNAKE_CASE | `MAX_PAGE_SIZE`, `DEFAULT_TIMEOUT` |
| Type Parameters | Single uppercase letter or PascalCase | `T`, `Request`, `Response` |

## Code Organization

- Group imports: Kotlin stdlib → external libraries → internal packages
- Order class members: companion object → properties → init → functions
- Keep functions small and focused (single responsibility)
- Use descriptive names over comments

## Immutability

- Prefer `val` over `var`
- Use `data class` for immutable data
- Avoid mutable collections in public APIs
- Return new instances instead of mutating

## Control Flow (Mandatory)

### Expression-Bodied Functions (Mandatory)

Always prefer expression syntax over block syntax:

```kotlin
// ✅ Preferred
fun calculate(x: Int): Int = x * 2

// ❌ Avoid
fun calculate(x: Int): Int {
    return x * 2
}
```

### Method Chaining (Mandatory)

Use method chaining for data transformation. **Never use `for` or `while` for transformations:**

```kotlin
// ✅ Preferred
repository
    .findAll()
    .map { it.toDomain() }
    .filter { it.isValid }

// ❌ Avoid: Never use imperative loops for data transformation
val items = repository.findAll()
val result = mutableListOf<Domain>()
for (item in items) {
    val domain = item.toDomain()
    if (domain.isValid) {
        result.add(domain)
    }
}

// ✅ Use fold/reduce/sumOf for aggregation
val total = items.filter { it.active }.sumOf { it.value }

// ✅ forEach is allowed for side effects only (no return value needed)
items.forEach { it.reset() }
```

### Result Type Pattern

Use `Result<T>` and method chaining for error handling:

```kotlin
// ✅ Preferred
Pagination
    .of(offset, size)
    .mapCatching { pagination ->
        repository.search(pagination)
    }.map { result ->
        Response(result)
    }

// ❌ Avoid
try {
    val pagination = Pagination.of(offset, size).getOrThrow()
    val result = repository.search(pagination)
    Result.success(Response(result))
} catch (e: Exception) {
    Result.failure(e)
}
```

## Error Handling

- Use `Result<T>` for expected failures in domain/application layers
- Use `require` for precondition checks
- Throw exceptions only for unexpected/programming errors
- Handle errors at appropriate boundaries (controllers)

## String Handling (Mandatory)

### String Interpolation (Mandatory)

Always use string templates for string construction. **Never use concatenation:**

```kotlin
// ❌ Avoid string concatenation
val message = "Hello, " + name + "!"
val path = baseUrl + "/api/" + endpoint

// ✅ Use string templates
val message = "Hello, $name!"
val path = "$baseUrl/api/$endpoint"

// ✅ Use ${} for expressions
val summary = "Total: ${items.size} items"
```

### Multiline Strings (Mandatory)

**Never use `\n` for line breaks.** Always use raw strings with trimIndent/trimMargin:

```kotlin
// ❌ Avoid escape sequences for newlines
val text = "Line 1\nLine 2\nLine 3"

// ✅ Use raw strings with trimIndent
val text = """
    Line 1
    Line 2
    Line 3
""".trimIndent()

// ✅ Use trimMargin for custom margin prefix
val message = """
    |Dear $name,
    |Thank you for your order.
""".trimMargin()
```

### Error Messages

Use string templates in error messages:

```kotlin
// ✅ Clear error messages with interpolation
require(offset >= 0) { "Offset must be non-negative, but got $offset" }
require(value in MIN..MAX) { "Value must be between $MIN and $MAX, but got $value" }
```

## Documentation

### Language

- All documentation must be written in English
- This includes: KDoc, code comments, commit messages, PR descriptions

### Tone

- Use formal, professional language
- Avoid casual or colloquial expressions

| ❌ Avoid | ✅ Preferred |
|----------|-------------|
| "This function is cool" | "This function performs X" |
| "Basically, it does..." | "This method executes..." |
| "Just a quick fix" | "Resolves issue with..." |

### Objectivity

- State facts and technical descriptions only
- Avoid subjective opinions or value judgments

| ❌ Avoid | ✅ Preferred |
|----------|-------------|
| "Great solution" | "Implements X using Y pattern" |
| "Ugly hack" | "Workaround for issue #123" |
| "Obviously" | Omit or explain the reasoning |

### KDoc Format

Use KDoc for public APIs:

```kotlin
/**
 * Brief description of the function.
 *
 * Additional details if necessary.
 *
 * @param name Description of parameter
 * @return Description of return value
 * @throws ExceptionType When this exception is thrown
 * @see RelatedClass
 */
fun someFunction(name: String): Result
```
