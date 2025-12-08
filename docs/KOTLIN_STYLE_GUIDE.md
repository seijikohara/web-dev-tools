# Kotlin Style Guide

This document describes the Kotlin coding conventions used in this project. These guidelines are based on [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) with project-specific customizations.

## Table of Contents

- [Naming Conventions](#naming-conventions)
- [File Organization](#file-organization)
- [Formatting](#formatting)
- [Documentation](#documentation)
- [Null Safety](#null-safety)
- [Functional Patterns](#functional-patterns)
- [Value Classes](#value-classes)
- [Coroutines](#coroutines)
- [Dependency Injection](#dependency-injection)
- [Error Handling](#error-handling)
- [Testing](#testing)

---

## Naming Conventions

### Classes and Interfaces

| Type | Convention | Example |
|------|------------|---------|
| Use Case Interface | `{Action}{Subject}UseCase` | `GetGeoLocationUseCase`, `SearchHtmlEntitiesUseCase` |
| Repository Interface | `{Subject}Repository` | `GeoIpRepository`, `HtmlEntityRepository` |
| Resolver Interface | `{Subject}Resolver` | `RdapServerResolver` |
| Adapter Implementation | `{Interface}Adapter` | `GeoIpRepositoryAdapter`, `RdapRepositoryAdapter` |
| Configuration | `{Feature}Configuration` | `ApplicationConfiguration`, `UseCaseConfiguration` |
| DTO | `{Subject}Dto` or `{Subject}ResponseDto` | `NetworkInfoResponseDto` |
| Exception | `{Subject}Exception` or `{Subject}NotFoundException` | `RdapServerNotFoundException` |

### Functions

| Type | Convention | Example |
|------|------------|---------|
| Factory function | `of(value)` returning `Result<T>` | `IpAddress.of("192.168.1.1")` |
| Conversion function | `toDomain()`, `toDto()`, `toEntity()` | `response.toDomain()` |
| Boolean function | `is{Condition}()`, `has{Property}()` | `isIpV4()`, `hasEntities()` |
| Bean factory | camelCase matching interface | `fun getGeoLocationUseCase(): GetGeoLocationUseCase` |

### Variables and Properties

- Use **camelCase** for all variables and properties
- Boolean properties use `is` prefix: `val isValid: Boolean`
- Nullable properties explicitly marked with `?`: `val city: String? = null`

### Constants

```kotlin
companion object {
    private const val MIN_SIZE = 1
    private const val MAX_SIZE = 100
    private const val DEFAULT_PAGE_SIZE = 20
}
```

---

## File Organization

### Package Structure

Follow [Clean Architecture](ARCHITECTURE.md) layering:

```
io.github.seijikohara.devtools
├── domain/                    # Business logic (no framework dependencies)
│   ├── common/
│   │   ├── extensions/        # Result extensions, utility functions
│   │   └── model/             # Shared domain models
│   └── {feature}/
│       ├── model/             # Domain entities, value objects
│       └── repository/        # Repository interfaces (ports)
├── application/               # Use cases (orchestration layer)
│   └── usecase/               # Use case interfaces and implementations
└── infrastructure/            # Framework-specific implementations
    ├── config/                # Spring configuration
    ├── database/              # Database adapters
    ├── externalapi/           # External API clients
    │   └── {service}/         # Per-service organization
    └── web/
        ├── controller/        # Web controllers
        └── dto/               # Request/Response DTOs
```

### Import Ordering

Imports should be ordered as follows (ktlint enforces this):

```kotlin
// 1. Kotlin standard library
import kotlin.collections.map

// 2. Java standard library
import java.net.URI
import java.time.Instant

// 3. Third-party libraries (alphabetically)
import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.Serializable
import org.springframework.stereotype.Controller

// 4. Project imports
import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.infrastructure.config.ApplicationProperties

// 5. Alias imports (when avoiding naming conflicts)
import io.github.seijikohara.devtools.infrastructure.externalapi.rdap.RdapEntity as ApiRdapEntity
```

### File-Level Annotations

Use sparingly for specific suppressions:

```kotlin
@file:Suppress("ktlint:standard:max-line-length")

package io.github.seijikohara.devtools.domain.networkinfo.model
```

---

## Formatting

### Indentation

- Use **4 spaces** for indentation (no tabs)
- Continuation indent: 4 spaces

### Line Length

- Target: **120 characters** maximum
- KDoc comments may exceed when necessary (suppress with `@file:Suppress`)

### Braces

Use K&R style (opening brace on same line):

```kotlin
class Example {
    fun doSomething(): Result<String> =
        runCatching {
            // implementation
        }
}
```

### Data Classes

```kotlin
data class GeoLocation(
    val ip: String? = null,
    val version: String? = null,
    val city: String? = null,
    val region: String? = null,
    val countryCode: String? = null,
)
```

### Function Chains

Break long chains for readability:

```kotlin
IpAddress
    .of(request.ipAddressString)
    .flatMap { ipAddress ->
        geoIpRepository(ipAddress)
    }.map { geoLocation ->
        Response(geoLocation)
    }
```

### Trailing Commas

Always use trailing commas in multi-line declarations:

```kotlin
data class Request(
    val name: String,
    val page: Int,
    val size: Int,  // trailing comma
)

enum class Status {
    PENDING,
    ACTIVE,
    COMPLETED,  // trailing comma
}
```

---

## Documentation

### KDoc Comments

Use KDoc for public APIs with meaningful documentation:

```kotlin
/**
 * Represents an IP address supporting both IPv4 and IPv6 formats.
 *
 * Wraps a validated IP address string using the `inet.ipaddr` library for validation.
 *
 * @property value The validated string representation of the IP address
 * @see <a href="https://datatracker.ietf.org/doc/rfc791/">RFC 791 - IPv4</a>
 * @see <a href="https://datatracker.ietf.org/doc/rfc8200/">RFC 8200 - IPv6</a>
 */
@JvmInline
value class IpAddress private constructor(
    val value: String,
) {
    /**
     * Creates an [IpAddress] from a string value with validation.
     *
     * @param value The string representation of an IP address
     * @return [Result.success] containing the [IpAddress] if valid,
     *         or [Result.failure] with [IllegalArgumentException] if invalid
     */
    companion object {
        fun of(value: String): Result<IpAddress> = ...
    }
}
```

### When to Document

| Element | Documentation Required |
|---------|----------------------|
| Public API | Always |
| Domain models | Always |
| Use case interfaces | Always |
| Repository interfaces | Always |
| Private implementation | Only when complex |
| Self-explanatory code | Not needed |

### Inline Comments

Avoid unnecessary comments. Code should be self-documenting:

```kotlin
// Bad - restates the code
val count = items.size  // Get the size of items

// Good - explains why
val count = items.size  // Cache size to avoid repeated collection traversal
```

---

## Null Safety

### Nullable Properties

Use nullable types for optional data, especially from external sources:

```kotlin
data class GeoLocation(
    val ip: String? = null,       // External API may not return
    val city: String? = null,     // Not all IPs have city data
    val timezone: String? = null,
)
```

### Safe Calls and Elvis Operator

```kotlin
// Chained safe calls
val cityName = response?.location?.city?.name

// Elvis operator for defaults
val displayName = user?.name ?: "Anonymous"

// takeUnless for conditional filtering
val nonBlankValue = value?.takeUnless { it.isBlank() }
```

### Scope Functions

```kotlin
// let for null-safe transformations
value?.let { json.decodeFromString(it) }

// also for side effects
result.also { logger.info("Result: $it") }

// run for object configuration
webClient.run {
    get()
        .uri(uri)
        .retrieve()
        .awaitBody<String>()
}
```

---

## Functional Patterns

### Result<T> Type

Use `Result<T>` for operations that can fail:

```kotlin
// Factory function returning Result
companion object {
    fun of(value: String): Result<IpAddress> =
        runCatching {
            require(value.isNotBlank()) { "IP address cannot be blank" }
            IpAddress(value)
        }
}

// Chaining with Result
IpAddress
    .of(ipString)
    .flatMap { repository.findByIp(it) }
    .map { Response(it) }
```

### Extension Functions

Define in appropriate scope:

```kotlin
// domain/common/extensions/ResultExtensions.kt
inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> =
    fold(
        onSuccess = transform,
        onFailure = { Result.failure(it) },
    )

// Private extension for local use
private fun IpApiCoResponse.toDomain(): GeoLocation =
    GeoLocation(
        ip = ip,
        city = city,
        // ...
    )
```

### Inline Functions with Reified Types

```kotlin
inline fun <reified T> decodeJson(value: String?): T? =
    value?.takeUnless { it.isBlank() }?.let { json.decodeFromString(it) }
```

---

## Value Classes

Use `@JvmInline value class` for type-safe wrappers:

```kotlin
@JvmInline
value class IpAddress private constructor(
    val value: String,
) {
    companion object {
        fun of(value: String): Result<IpAddress> =
            runCatching {
                require(value.isNotBlank()) { "IP address cannot be blank" }
                // Validation logic
                IpAddress(value)
            }
    }

    fun isIpV4(): Boolean = ...
    fun isIpV6(): Boolean = ...
}

@JvmInline
value class CountryCode private constructor(
    val value: String,
) {
    companion object {
        fun of(value: String): Result<CountryCode> =
            runCatching {
                require(value.length == 2) { "Country code must be 2 characters" }
                require(value.all { it.isUpperCase() && it.isLetter() })
                CountryCode(value)
            }
    }
}
```

### Value Class Guidelines

1. Use `private constructor` to enforce validation
2. Provide `of()` factory returning `Result<T>`
3. Add domain-specific methods as member functions
4. Keep immutable (no `var` properties)

---

## Coroutines

### Suspend Functions

Use `suspend` for all I/O operations:

```kotlin
// Repository interface
fun interface GeoIpRepository {
    suspend operator fun invoke(ipAddress: IpAddress): Result<GeoLocation>
}

// Use case interface
fun interface GetGeoLocationUseCase {
    suspend operator fun invoke(request: Request): Result<Response>
}
```

### Functional Interface with operator fun

Use `fun interface` with `operator fun invoke` for single-method interfaces:

```kotlin
fun interface GetGeoLocationUseCase {
    suspend operator fun invoke(request: Request): Result<Response>

    data class Request(val ipAddressString: String)
    data class Response(val geoLocation: GeoLocation)
}

// Usage - can be called like a function
val result = useCase(request)
```

### WebClient with Coroutines

```kotlin
override suspend fun invoke(ipAddress: IpAddress): Result<GeoLocation> =
    runCatching { buildUri(ipAddress) }
        .onSuccess { uri -> logger.info("[GEO] Fetching: $uri") }
        .mapCatching { uri ->
            webClient
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .awaitBody<String>()
        }.mapCatching { json ->
            decodeJson<IpApiCoResponse>(json)
                ?: throw IllegalStateException("Failed to decode response")
        }.map { it.toDomain() }
```

---

## Dependency Injection

### Configuration Classes

```kotlin
@Configuration
class UseCaseConfiguration {
    @Bean
    fun getGeoLocationUseCase(geoIpRepository: GeoIpRepository): GetGeoLocationUseCase =
        getGeoLocationUseCase(geoIpRepository)  // Call factory function
}

@Configuration
class RepositoryConfiguration(
    private val applicationProperties: ApplicationProperties,
) {
    @Bean
    fun geoIpRepository(webClient: WebClient): GeoIpRepository =
        GeoIpRepositoryAdapter(webClient, applicationProperties.network.geoIp.baseUrl)
}
```

### Factory Functions (Application Layer)

Keep application layer free of Spring annotations:

```kotlin
// application/usecase/GetGeoLocationUseCase.kt
fun getGeoLocationUseCase(geoIpRepository: GeoIpRepository): GetGeoLocationUseCase =
    GetGeoLocationUseCase { request ->
        IpAddress
            .of(request.ipAddressString)
            .flatMap { ipAddress -> geoIpRepository(ipAddress) }
            .map { geoLocation -> GetGeoLocationUseCase.Response(geoLocation) }
    }
```

### ConfigurationProperties

```kotlin
@ConfigurationProperties(prefix = "application")
data class ApplicationProperties(
    val apiBasePath: String,
    val indexFile: Resource,
    val cors: CorsProperties,
    val network: NetworkProperties,
) {
    data class CorsProperties(
        val mappingPathPattern: String,
        val allowedOrigins: List<String>,
    )

    data class NetworkProperties(
        val geoIp: GeoIpProperties,
        val rdap: RdapProperties,
    )
}
```

---

## Error Handling

### Result-Based Error Handling

```kotlin
// Chain operations with Result
override suspend fun invoke(request: Request): Result<Response> =
    IpAddress
        .of(request.ipAddressString)
        .flatMap { ipAddress -> repository(ipAddress) }
        .map { data -> Response(data) }
```

### Controller Error Handling

```kotlin
@GetMapping("/network/{ip}")
suspend fun getNetworkInfo(
    @PathVariable ip: String,
): NetworkInfoResponseDto =
    getGeoLocationUseCase(GetGeoLocationUseCase.Request(ipAddressString = ip))
        .fold(
            onSuccess = { response -> response.toDto() },
            onFailure = { error ->
                when (error) {
                    is IllegalArgumentException ->
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, error.message)
                    is WebClientResponseException.NotFound ->
                        throw ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found")
                    else -> throw error
                }
            },
        )
```

### Custom Exceptions

```kotlin
class RdapServerNotFoundException(
    ipAddress: IpAddress,
) : RuntimeException("No RDAP server found for IP address: ${ipAddress.value}")
```

---

## Testing

### Test Framework

Use **Kotest** with **FunSpec** style and **Mockk** for mocking:

```kotlin
class GetGeoLocationUseCaseSpec :
    FunSpec({
        context("GetGeoLocationUseCase execution") {
            test("should return geo location for valid IP") {
                // Arrange
                val mockRepository = mockk<GeoIpRepository>()
                coEvery { mockRepository(any()) } returns Result.success(expectedGeoLocation)

                val useCase = getGeoLocationUseCase(mockRepository)

                // Act
                val result = useCase(Request(ipAddressString = "8.8.8.8"))

                // Assert
                result.isSuccess shouldBe true
                result.getOrNull()?.geoLocation shouldBe expectedGeoLocation

                coVerify(exactly = 1) { mockRepository(any()) }
            }
        }
    })
```

### Data-Driven Testing

```kotlin
context("IP address validation") {
    data class IpTestCase(
        val input: String,
        val expectedValid: Boolean,
        val description: String,
    )

    withData(
        nameFn = { it.description },
        IpTestCase("192.168.1.1", true, "valid IPv4"),
        IpTestCase("2001:db8::1", true, "valid IPv6"),
        IpTestCase("invalid", false, "invalid format"),
        IpTestCase("", false, "empty string"),
    ) { (input, expectedValid, _) ->
        val result = IpAddress.of(input)
        result.isSuccess shouldBe expectedValid
    }
}
```

### Test Naming

- Use descriptive context and test names
- Follow "should {expected behavior} when {condition}" pattern:

```kotlin
context("IpAddress validation") {
    test("should create valid IpAddress for IPv4") { ... }
    test("should fail for blank input") { ... }
    test("should fail for malformed address") { ... }
}
```

### Mockk Conventions

| Function | Purpose |
|----------|---------|
| `mockk<T>()` | Create a mock |
| `coEvery { }` | Stub suspend function |
| `every { }` | Stub regular function |
| `coVerify { }` | Verify suspend function call |
| `verify { }` | Verify regular function call |

---

## Type Aliases

Use for simple DTO mappings:

```kotlin
// When DTO is identical to domain model
typealias GeoResponseDto = GeoLocation
typealias RdapResponseDto = RdapInformation
```

---

## Linting and Formatting

### ktlint

The project uses ktlint for code formatting. Run before committing:

```bash
./gradlew ktlintCheck    # Check formatting
./gradlew ktlintFormat   # Auto-fix formatting
```

### Suppression

Use file-level suppression sparingly:

```kotlin
@file:Suppress("ktlint:standard:max-line-length")
```

---

## References

- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Project Architecture Guide](ARCHITECTURE.md)
- [Kotest Documentation](https://kotest.io/)
- [Mockk Documentation](https://mockk.io/)
- [ktlint](https://pinterest.github.io/ktlint/)
