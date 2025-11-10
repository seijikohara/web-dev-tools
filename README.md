# web-dev-tools

[![GitHub Actions](https://github.com/seijikohara/web-dev-tools/actions/workflows/ci.yml/badge.svg)](https://github.com/seijikohara/web-dev-tools/actions)
![Last Commit](https://img.shields.io/github/last-commit/seijikohara/web-dev-tools)
![GitHub top language](https://img.shields.io/github/languages/top/seijikohara/web-dev-tools)

Personal project for technical learning and experimentation, providing web developer utilities including HTML entity reference, IP geolocation, RDAP information, and HTTP diagnostics.

- **Backend**: Reactive architecture with Kotlin coroutines and Spring WebFlux, implementing clean architecture principles and domain-driven design with non-blocking I/O throughout the stack
- **Frontend**: Type-safe single-page application with Vue 3 Composition API and TypeScript, featuring component-based UI and modern build tooling

## Tech Stack

### Backend

#### Core
- **[Java 21](https://openjdk.org/projects/jdk/21/)** - Runtime environment with virtual threads and pattern matching
- **[Kotlin 2](https://kotlinlang.org/)** - Type-safe JVM language with coroutines and compiler plugins

#### Framework
- **[Spring Boot 3](https://spring.io/projects/spring-boot)** - Production-ready framework with reactive web support (WebFlux, R2DBC, Actuator)
- **[Reactor Kotlin Extensions](https://projectreactor.io/docs/kotlin/release/reference/)** - Kotlin extension functions for Project Reactor

#### Libraries
- **[Guava](https://github.com/google/guava)** - Google's core Java libraries for collections and utilities
- **[IPAddress](https://github.com/seancfoley/IPAddress)** - Java library for IP address manipulation and CIDR notation

#### Build & Database
- **[Gradle 9](https://docs.gradle.org/current/userguide/userguide.html)** - Build automation tool with Kotlin DSL and incremental compilation
- **[Flyway](https://flywaydb.org/)** - Database migration tool with version control
- **[H2](https://www.h2database.com/)** - Embedded SQL database for development and testing

#### Code Quality
- **[Spotless](https://github.com/diffplug/spotless)** - Code formatter with ktlint integration for Kotlin

#### Testing
- **[Kotest](https://kotest.io/)** - Kotlin testing framework with multiple test styles and property-based testing
- **[MockK](https://mockk.io/)** - Mocking library for Kotlin with coroutine support
- **[SpringMockK](https://github.com/Ninja-Squad/springmockk)** - MockK integration for Spring dependency injection

### Frontend

#### Core
- **[Node.js 24](https://nodejs.org/)** - JavaScript runtime with V8 engine and ES modules support
- **[TypeScript](https://www.typescriptlang.org/)** - Statically typed JavaScript with compile-time type checking
- **[Vite](https://vitejs.dev/)** - Fast build tool with native ES modules and HMR

#### HTTP Client
- **[Axios](https://axios-http.com/)** - Promise-based HTTP client for REST API communication

#### Framework
- **[Vue.js 3](https://v3.vuejs.org/)** - Progressive framework with Composition API and reactive system
  - [Vue Router](https://router.vuejs.org/) - Official routing library for SPA navigation
  - [Pinia](https://pinia.vuejs.org/) - Type-safe state management with modular stores
  - [PrimeVue](https://www.primefaces.org/primevue/) - Enterprise-grade UI component library

#### Utilities
- **[VueUse](https://vueuse.org/)** - Composition API utility functions for Vue 3
- **[Sass](https://sass-lang.com/)** - CSS preprocessor with variables and mixins

#### Code Quality
- **[ESLint](https://eslint.org/)** - JavaScript/TypeScript linter with configurable rules
- **[Prettier](https://prettier.io/)** - Opinionated code formatter with consistent styling
- **[vue-tsc](https://github.com/vuejs/language-tools)** - TypeScript type checker for Vue single-file components

#### Testing
- **[Playwright](https://playwright.dev/)** - Cross-browser E2E testing framework with auto-wait

### Development & CI/CD

- **[GitHub Actions](https://github.com/features/actions)** - Automated CI/CD workflows for build, test, and deployment

### Infrastructure

- **[Docker](https://www.docker.com/)** - Container platform with multi-stage builds and distroless images (Java 25 runtime)
