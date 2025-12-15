# Clean Architecture Guidelines

## Layer Overview

This project follows Clean Architecture with three main layers:

```
┌─────────────────────────────────────────────────────────┐
│                Infrastructure Layer                      │
│  web/controllers, database/adapters, externalapi/       │
│  config/, DTOs                                          │
├─────────────────────────────────────────────────────────┤
│                Application Layer                         │
│  usecase/ (business logic orchestration)                │
├─────────────────────────────────────────────────────────┤
│                Domain Layer                              │
│  model/ (entities, value objects)                       │
│  repository/ (interfaces only)                          │
└─────────────────────────────────────────────────────────┘
```

## Dependency Rules (Enforced by Tests)

### Domain Layer

**Location**: `domain/`

**MUST NOT depend on:**
- Infrastructure layer (`infrastructure.*`)
- Application layer (`application.*`)
- Spring Framework (`org.springframework.*`)
- Database libraries (`org.springframework.data.*`, `io.r2dbc.*`)
- Web libraries (`org.springframework.web.*`, `jakarta.servlet.*`)

**Contains:**
- Entities and Value Objects
- Repository interfaces (contracts only)
- Domain services (pure business logic)

```kotlin
// ✅ Correct - Domain layer
package io.github.seijikohara.devtools.domain.htmlreference.model

data class HtmlEntity(
    val id: Long,
    val name: String,
    val code: EntityCode,
    // ...
)
```

### Application Layer

**Location**: `application/usecase/`

**MUST NOT depend on:**
- Infrastructure layer (`infrastructure.*`)

**CAN depend on:**
- Domain layer (`domain.*`)
- Kotlin stdlib and coroutines

**Contains:**
- Use cases (business logic orchestration)
- Application services

```kotlin
// ✅ Correct - Application layer
package io.github.seijikohara.devtools.application.usecase

import io.github.seijikohara.devtools.domain.htmlreference.repository.HtmlEntityRepository

fun interface SearchHtmlEntitiesUseCase {
    suspend operator fun invoke(request: Request): Result<Response>
    // ...
}
```

### Infrastructure Layer

**Location**: `infrastructure/`

**CAN depend on:**
- Application layer (`application.*`)
- Domain layer (`domain.*`)
- External libraries (Spring, R2DBC, etc.)

**Contains:**
- Repository implementations (adapters)
- REST controllers
- DTOs (Data Transfer Objects)
- Configuration classes
- External API clients

```kotlin
// ✅ Correct - Infrastructure layer
package io.github.seijikohara.devtools.infrastructure.database.htmlreference

import io.github.seijikohara.devtools.domain.htmlreference.repository.HtmlEntityRepository

class HtmlEntityRepositoryAdapter(
    private val dbRepository: HtmlEntityDbRepository,
) : HtmlEntityRepository {
    // Implementation
}
```

## Package Structure

```
io.github.seijikohara.devtools/
├── application/
│   └── usecase/              # Use cases
├── domain/
│   ├── common/               # Shared domain components
│   │   ├── extensions/       # Extension functions
│   │   └── model/            # Common value objects
│   ├── dns/
│   │   ├── model/            # DNS-related entities
│   │   └── repository/       # DNS repository interface
│   ├── htmlreference/
│   │   ├── model/            # HTML reference entities
│   │   └── repository/       # HTML repository interface
│   └── networkinfo/
│       ├── model/            # Network info entities
│       └── repository/       # Network repository interfaces
└── infrastructure/
    ├── config/               # Spring configurations
    ├── database/             # Database adapters
    │   └── htmlreference/    # HTML entity DB adapter
    ├── externalapi/          # External API adapters
    │   ├── common/           # Shared API utilities
    │   ├── dns/              # DNS API adapter
    │   ├── geoip/            # GeoIP API adapter
    │   └── rdap/             # RDAP API adapter
    └── web/
        ├── controller/       # REST controllers
        └── dto/              # DTOs
```

## Naming Conventions (Enforced by Tests)

| Type | Pattern | Location |
|------|---------|----------|
| Repository Interface | `*Repository` or `*Resolver` | `domain/**/repository/` |
| Use Case Interface | `*UseCase` | `application/usecase/` |
| Repository Implementation | `*Adapter` | `infrastructure/**/` |
| Configuration Class | `*Configuration` or `*Config` | `infrastructure/config/` |

## Data Flow

```
Controller → UseCase → Repository(Interface) → Adapter → External System
     ↓           ↓              ↓                  ↓
    DTO      Request/      Domain Entity     DB Entity/
             Response                        API Response
```

### Example Flow

1. **Controller** receives HTTP request, maps to UseCase.Request
2. **UseCase** orchestrates business logic using domain interfaces
3. **Repository (interface)** defines contract in domain layer
4. **Adapter** implements repository, calls external systems
5. **Adapter** maps external response to domain entity
6. **UseCase** maps domain entity to Response
7. **Controller** maps Response to DTO

## Architecture Tests

Architecture rules are enforced by Konsist tests in:
- `src/test/kotlin/.../architecture/CleanArchitectureSpec.kt`
- `src/test/kotlin/.../architecture/NamingConventionSpec.kt`

Run with: `./gradlew test`
