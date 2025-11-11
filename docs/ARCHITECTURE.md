# Architecture Guide

This project follows **Clean Architecture** and **Domain-Driven Design (DDD)** principles to maintain a clean separation of concerns and prevent architectural erosion.

## Layer Structure

```
┌─────────────────────────────────────┐
│       Infrastructure Layer          │
│  (Web, Database, External APIs)     │
│                                     │
│  ┌───────────────────────────────┐ │
│  │     Application Layer         │ │
│  │     (Use Cases)               │ │
│  │                               │ │
│  │  ┌─────────────────────────┐ │ │
│  │  │    Domain Layer         │ │ │
│  │  │  (Business Logic)       │ │ │
│  │  └─────────────────────────┘ │ │
│  └───────────────────────────────┘ │
└─────────────────────────────────────┘
```

### Domain Layer (`domain`)
**Purpose**: Contains core business logic and domain models

**Rules**:
- ✅ May only depend on itself and standard library (Kotlin/Java)
- ❌ Must NOT depend on infrastructure layer
- ❌ Must NOT depend on Spring Framework
- ❌ Must NOT depend on database libraries
- ❌ Must NOT depend on web frameworks

**Contents**:
- Domain models (entities, value objects)
- Repository interfaces (ports)
- Domain services
- Domain events

### Application Layer (`application`)
**Purpose**: Orchestrates domain logic to fulfill use cases

**Rules**:
- ✅ May depend on domain layer
- ❌ Must NOT depend on infrastructure layer
- Uses factory functions for dependency injection

**Contents**:
- Use case implementations
- Application services
- DTOs for use case inputs/outputs

### Infrastructure Layer (`infrastructure`)
**Purpose**: Implements technical details and external integrations

**Rules**:
- ✅ May depend on application and domain layers
- Implements repository interfaces (adapters)
- Handles framework-specific code

**Contents**:
- Database adapters (implements repository interfaces)
- Web controllers and handlers
- External API clients
- Configuration classes

## Preventing Architecture Violations

### 1. Automated Testing with Kotest + ArchUnit

The project uses **Kotest** (idiomatic Kotlin testing framework) combined with **ArchUnit** for architecture verification.

```kotlin
./gradlew test
```

#### Test Structure with Kotest FunSpec

All architecture tests use **Kotest FunSpec** for consistency and simplicity.

**Basic Layer Dependency Test** (`CleanArchitectureSpec.kt`):
```kotlin
test("domain layer should not depend on infrastructure layer") {
    noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
        .check(classes)
}
```

**Data-Driven Testing** (`PackageDependencySpec.kt`):
```kotlin
describe("Domain layer forbidden dependencies") {
    withData(
        ForbiddenDependency("Spring Framework", listOf("org.springframework..")),
        ForbiddenDependency("R2DBC", listOf("io.r2dbc..")),
        // ... more dependencies
    ) { (name, packages) ->
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(*packages.toTypedArray())
            .check(classes)
    }
}
```

**Naming Convention Test** (`NamingConventionSpec.kt`):
```kotlin
context("Domain layer naming conventions") {
    test("repository interfaces should end with 'Repository'") {
        classes().that().resideInAPackage("..domain..repository..")
            .and().areInterfaces()
            .should().haveSimpleNameEndingWith("Repository")
            .check(classes)
    }
}
```

#### Benefits of Kotest + ArchUnit

1. **Multiple Test Styles**: Choose the style that fits your needs (FunSpec, BehaviorSpec, DescribeSpec, etc.)
2. **Data-Driven Testing**: Test multiple scenarios with `withData` for clearer violation reports
3. **Idiomatic Kotlin**: Natural Kotlin syntax with DSL
4. **Better Test Names**: Kotest generates readable test names from string descriptions
5. **Context and Hooks**: `beforeSpec`, `afterSpec`, nested contexts

These tests automatically detect violations:
- Domain layer depending on infrastructure
- Application layer depending on infrastructure
- Domain layer using Spring/database/web frameworks
- Naming convention violations

**Build will fail** if any violations are detected, preventing architectural erosion.

### 2. Dependency Direction Rule

Always follow the **Dependency Inversion Principle**:

```
Infrastructure → Application → Domain
                              ↑
                    (no reverse dependencies)
```

### 3. Adapter Pattern

Use adapters to bridge domain interfaces with infrastructure:

**Good Example**:
```kotlin
// Domain layer - defines interface
interface HtmlEntityRepository {
    suspend fun searchByName(name: String, pagination: Pagination): PaginatedResult<HtmlEntity>
}

// Infrastructure layer - implements interface
class HtmlEntityRepositoryAdapter(
    private val dbRepository: HtmlEntityDbRepository
) : HtmlEntityRepository {
    // Implementation details
}
```

**Bad Example**:
```kotlin
// Domain layer directly using Spring Data
interface HtmlEntityRepository : CoroutineCrudRepository<HtmlEntity, Long> {
    // ❌ Violates dependency rule!
}
```

### 4. Factory Functions for Use Cases

Application layer uses factory functions to avoid Spring annotations:

```kotlin
// Application layer
fun searchHtmlEntitiesUseCase(repository: HtmlEntityRepository): SearchHtmlEntitiesUseCase =
    SearchHtmlEntitiesUseCase { query ->
        repository.searchByName(query.name, query.pagination)
    }

// Infrastructure configuration
@Bean
fun searchHtmlEntitiesUseCase(repository: HtmlEntityRepository) =
    createSearchHtmlEntitiesUseCase(repository)
```

### 5. Expression Chains for Functional Purity

Avoid intermediate variables; use expression chains:

```kotlin
// Good - pure expression chain
fun toDomain(): Result<HtmlEntity> =
    runCatching { requireNotNull(id) }
        .flatMap { validId ->
            EntityCode.of(code)
                .map { validCode ->
                    HtmlEntity(id = validId, code = validCode, ...)
                }
        }

// Avoid - intermediate variables
fun toDomain(): Result<HtmlEntity> {
    val validId = requireNotNull(id)
    val validCode = EntityCode.of(code).getOrThrow()
    return Result.success(HtmlEntity(id = validId, code = validCode, ...))
}
```

## Module Organization

Consider splitting into Gradle modules for stronger enforcement:

```
project/
├── domain/           # Pure domain logic
├── application/      # Use cases
└── infrastructure/   # Spring Boot, database, web
```

This makes violations **impossible to compile** rather than just test failures.

## Common Pitfalls

### ❌ Domain model with Spring annotations
```kotlin
@Entity  // Spring Data annotation
data class HtmlEntity(...)
```

### ✅ Correct: Separate domain and database models
```kotlin
// Domain layer
data class HtmlEntity(val id: Long, val name: String, ...)

// Infrastructure layer
@Table("html_entities")
data class HtmlEntityDbEntity(val id: Long?, val name: String, ...)
```

### ❌ Domain service with @Service annotation
```kotlin
@Service  // Spring annotation
class DomainService(...)
```

### ✅ Correct: Pure domain service
```kotlin
// Domain layer - no annotations
class DomainService {
    fun calculate(...): Result<T>
}
```

## Continuous Verification

1. **Pre-commit**: Run tests locally
```bash
./gradlew test
```

2. **CI/CD**: Architecture tests run on every PR
3. **Code Review**: Check for proper layer separation
4. **Refactoring**: Keep adapter/extension files separate from configuration

## References

- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [ArchUnit Documentation](https://www.archunit.org/)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
