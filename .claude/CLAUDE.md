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
See `rules/architecture/clean-architecture.md` for detailed guidelines.

Key rules (enforced by Konsist tests):
- Domain layer MUST NOT depend on infrastructure or application layers
- Application layer MUST NOT depend on infrastructure layer
- Domain layer MUST NOT use Spring Framework annotations

## Key Conventions

See `rules/kotlin/conventions.md` for detailed Kotlin coding guidelines.

### Code Style

- ktlint formatting (enforced by Spotless)
- KDoc comments for public APIs
- Use `suspend` functions for async operations
- Use `Result<T>` for error handling in domain/application layers

### Control Flow

- Prefer expression-bodied functions (mandatory)
- Use method chaining (`.map`, `.mapCatching`, `.fold`) - never use `for`/`while` for transformations
- Use string templates - never use string concatenation

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

Follow Conventional Commits format. See `rules/general/git.md` for detailed guidelines.
