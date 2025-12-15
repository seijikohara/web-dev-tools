# Web Dev Tools Backend - Project Instructions

## Project Overview

A web developer tools application backend built with Kotlin, Spring Boot WebFlux, and Clean Architecture.

See @README.md for detailed project documentation.
See @build.gradle.kts for build configuration and dependencies.

## Important Notice

**The `frontend/` directory is a Git submodule and is NOT managed by this project.**
Do NOT modify, create, or delete any files under `frontend/`.
Frontend-related changes should be made in the frontend repository separately.

## Quick Reference

### Development Commands

```bash
./gradlew build                    # Build project (excludes integration tests)
./gradlew check                    # Run all tests including integration tests
./gradlew test                     # Run unit tests only
./gradlew integrationTest          # Run integration tests only
./gradlew spotlessApply            # Format code with ktlint
./gradlew spotlessCheck            # Check code formatting
./gradlew bootRun                  # Run application
./gradlew dependencies             # Show dependency tree
```

### Project Structure

```
src/
├── main/kotlin/io/github/seijikohara/devtools/
│   ├── application/usecase/      # Use cases (business logic orchestration)
│   ├── domain/                    # Domain layer (entities, value objects, repositories)
│   │   ├── common/               # Shared domain components
│   │   ├── dns/                  # DNS domain
│   │   ├── htmlreference/        # HTML reference domain
│   │   └── networkinfo/          # Network info domain
│   └── infrastructure/            # Infrastructure layer
│       ├── config/               # Spring configurations
│       ├── database/             # Database adapters
│       ├── externalapi/          # External API adapters
│       └── web/                  # REST controllers, DTOs
├── test/kotlin/                   # Unit tests
└── it/kotlin/                     # Integration tests
```

## Technology Stack

- **Language**: Kotlin 2.x (JDK 21)
- **Framework**: Spring Boot 3.x with WebFlux (reactive)
- **Build**: Gradle Kotlin DSL with version catalogs
- **Database**: H2 with R2DBC (reactive), Flyway migrations
- **Testing**: Kotest (FunSpec), MockK, Konsist (architecture tests)
- **Code Style**: ktlint via Spotless
- **Serialization**: kotlinx.serialization
- **Coroutines**: kotlinx.coroutines with Reactor integration
- **API Docs**: SpringDoc OpenAPI

## Architecture Principles

This project follows **Clean Architecture** and **Domain-Driven Design (DDD)** principles.

### Layer Dependencies

```
┌─────────────────────────────────────┐
│        Infrastructure Layer         │
│  (controllers, adapters, configs)   │
├─────────────────────────────────────┤
│        Application Layer            │
│        (use cases)                  │
├─────────────────────────────────────┤
│          Domain Layer               │
│  (entities, value objects, repos)   │
└─────────────────────────────────────┘
```

**Strict Rules:**
- Domain layer MUST NOT depend on infrastructure or application layers
- Application layer MUST NOT depend on infrastructure layer
- Domain layer MUST NOT use Spring Framework annotations
- These rules are enforced by Konsist architecture tests

### Use Case Pattern

Use cases are defined as functional interfaces (`fun interface`) with factory functions:

```kotlin
fun interface GetSomethingUseCase {
    suspend operator fun invoke(request: Request): Result<Response>

    data class Request(...)
    data class Response(...)
}

fun getSomethingUseCase(repository: SomeRepository): GetSomethingUseCase =
    GetSomethingUseCase { request ->
        // Implementation using Result type for error handling
    }
```

### Repository Pattern

- Domain layer defines repository interfaces
- Infrastructure layer provides adapter implementations
- Adapters must end with `Adapter` suffix (enforced by tests)

### Value Objects

Use `Result<T>` for validation in factory methods:

```kotlin
data class SomeValueObject(val value: Int) {
    companion object {
        fun of(value: Int): Result<SomeValueObject> = runCatching {
            require(value > 0) { "Value must be positive" }
            SomeValueObject(value)
        }
    }
}
```

## Naming Conventions

| Type | Convention | Example |
|------|------------|---------|
| Use Cases | `XxxUseCase` | `SearchHtmlEntitiesUseCase` |
| Repositories | `XxxRepository` or `XxxResolver` | `HtmlEntityRepository` |
| Adapters | `XxxAdapter` | `HtmlEntityRepositoryAdapter` |
| Configurations | `XxxConfiguration` or `XxxConfig` | `RepositoryConfiguration` |
| DTOs | `XxxDto` | `HtmlEntitySearchResponseDto` |
| Test classes | `XxxSpec` | `SearchHtmlEntitiesUseCaseSpec` |

## Key Conventions

### Code Style

- ktlint formatting (enforced by Spotless)
- KDoc comments for public APIs
- Use `suspend` functions for async operations
- Use `Result<T>` for error handling in domain/application layers

### Control Flow

- Prefer expression-bodied functions
- Use method chaining (`.map`, `.mapCatching`, `.fold`)
- Use early returns for validation

### Documentation

All documentation (including code comments, commit messages, PR descriptions) must:

- **Language**: Always use English
- **Tone**: Use formal, professional language
- **Style**: KDoc format for public APIs

### Documentation Consistency

**Before creating a PR**, verify code and documentation are consistent:

- Code changes → Update related documentation (CLAUDE.md, architecture docs)
- Documentation changes → Verify against actual code
- Run command examples to ensure they work
- Check class/method names match exactly

See `rules/general/pr.md` for detailed verification steps.

## Git Conventions

Follow Conventional Commits format:

```
<type>(<scope>): <description>
```

### Types

| Type | Description |
|------|-------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation only |
| `style` | Formatting, no code change |
| `refactor` | Code change without feature/fix |
| `perf` | Performance improvement |
| `test` | Adding/updating tests |
| `chore` | Build, tooling, dependencies |
| `build` | Build system changes |

### Scope Examples

- `usecase` - Use case changes
- `domain` - Domain layer changes
- `infra` - Infrastructure changes
- `api` - API/controller changes
- `deps` - Dependency updates
