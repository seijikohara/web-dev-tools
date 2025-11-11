# Kotest + ArchUnit Integration Guide

This guide demonstrates how to use Kotest with ArchUnit for architecture testing in Kotlin projects.

## Why Kotest + ArchUnit?

### ArchUnit Alone (JUnit5)
```kotlin
class CleanArchitectureTest {
    @Test
    fun `domain should not depend on infrastructure`() {
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("..infrastructure..")
            .check(classes)
    }
}
```

### Kotest + ArchUnit
```kotlin
class CleanArchitectureSpec : FunSpec({
    test("domain should not depend on infrastructure") {
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("..infrastructure..")
            .check(classes)
    }
})
```

## Benefits

1. **No annotations** - Pure Kotlin code
2. **Multiple test styles** - FunSpec, BehaviorSpec, DescribeSpec, StringSpec, etc.
3. **Data-driven testing** - Test multiple scenarios with `withData`
4. **Better hooks** - `beforeSpec`, `afterSpec`, `beforeTest`, `afterTest`
5. **Nested contexts** - Organize tests hierarchically
6. **Idiomatic Kotlin** - DSL syntax feels natural

## Test Style Examples

### 1. FunSpec (Recommended)

```kotlin
class CleanArchitectureSpec : FunSpec({
    lateinit var classes: JavaClasses

    beforeSpec {
        classes = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackages("com.example")
    }

    context("Clean Architecture layer dependencies") {
        test("domain should not depend on infrastructure") {
            noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAPackage("..infrastructure..")
                .check(classes)
        }

        test("application should not depend on infrastructure") {
            noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat()
                .resideInAPackage("..infrastructure..")
                .check(classes)
        }
    }
})
```

### 2. Data-Driven Testing with DescribeSpec

```kotlin
class PackageDependencySpec : DescribeSpec({
    val classes = ClassFileImporter()
        .withImportOption(ImportOption.DoNotIncludeTests())
        .importPackages("com.example")

    describe("Domain layer forbidden dependencies") {
        data class ForbiddenDependency(
            val name: String,
            val packages: List<String>
        )

        withData(
            nameFn = { it.name },
            ForbiddenDependency(
                name = "Spring Framework",
                packages = listOf("org.springframework..")
            ),
            ForbiddenDependency(
                name = "Database libraries",
                packages = listOf("org.springframework.data..", "io.r2dbc..")
            ),
            ForbiddenDependency(
                name = "Web libraries",
                packages = listOf("org.springframework.web..", "jakarta.servlet..")
            )
        ) { (_, packages) ->
            noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(*packages.toTypedArray())
                .check(classes)
        }
    }
})
```

**Output when a test fails:**
```
PackageDependencySpec > Domain layer forbidden dependencies > Spring Framework FAILED
PackageDependencySpec > Domain layer forbidden dependencies > Database libraries PASSED
PackageDependencySpec > Domain layer forbidden dependencies > Web libraries PASSED
```

Each dependency is tested individually with a clear name!

### 3. BehaviorSpec (BDD Style)

```kotlin
class NamingConventionSpec : BehaviorSpec({
    val classes = ClassFileImporter()
        .withImportOption(ImportOption.DoNotIncludeTests())
        .importPackages("com.example")

    given("repository interfaces in domain layer") {
        `when`("checking naming convention") {
            then("should end with 'Repository'") {
                classes()
                    .that().resideInAPackage("..domain..repository..")
                    .and().areInterfaces()
                    .should().haveSimpleNameEndingWith("Repository")
                    .check(classes)
            }
        }
    }

    given("configuration classes") {
        `when`("checking naming convention") {
            then("should end with 'Configuration' or 'Config'") {
                classes()
                    .that().areAnnotatedWith(Configuration::class.java)
                    .should().haveSimpleNameEndingWith("Configuration")
                    .orShould().haveSimpleNameEndingWith("Config")
                    .check(classes)
            }
        }
    }
})
```

### 4. StringSpec (Simplest)

```kotlin
class SimpleArchitectureSpec : StringSpec({
    val classes = ClassFileImporter()
        .withImportOption(ImportOption.DoNotIncludeTests())
        .importPackages("com.example")

    "domain should not depend on infrastructure" {
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("..infrastructure..")
            .check(classes)
    }

    "domain should not depend on Spring Framework" {
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("org.springframework..")
            .check(classes)
    }
})
```

## Common Patterns

### Pattern 1: Layered Architecture

```kotlin
test("layered architecture should be respected") {
    layeredArchitecture()
        .consideringAllDependencies()
        .layer("Domain").definedBy("..domain..")
        .layer("Application").definedBy("..application..")
        .layer("Infrastructure").definedBy("..infrastructure..")
        .whereLayer("Domain").mayNotAccessAnyLayer()
        .whereLayer("Application").mayOnlyAccessLayers("Domain")
        .whereLayer("Infrastructure").mayOnlyAccessLayers("Application", "Domain")
        .check(classes)
}
```

### Pattern 2: Dependency Rules with Multiple Packages

```kotlin
test("domain should not depend on infrastructure concerns") {
    noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat()
        .resideInAnyPackage(
            "org.springframework..",
            "org.springframework.data..",
            "io.r2dbc..",
            "org.springframework.web..",
            "jakarta.servlet..",
            "..infrastructure.."
        ).check(classes)
}
```

### Pattern 3: Naming Conventions

```kotlin
test("repositories should end with 'Repository'") {
    classes()
        .that().resideInAPackage("..domain..repository..")
        .and().areInterfaces()
        .should().haveSimpleNameEndingWith("Repository")
        .check(classes)
}

test("adapters should end with 'Adapter'") {
    classes()
        .that().resideInAPackage("..infrastructure..")
        .and().haveSimpleNameContaining("Repository")
        .and().areNotInterfaces()
        .should().haveSimpleNameEndingWith("Adapter")
        .check(classes)
}
```

### Pattern 4: Annotation Rules

```kotlin
test("configuration classes must have @Configuration annotation") {
    classes()
        .that().haveSimpleNameEndingWith("Configuration")
        .should().beAnnotatedWith(Configuration::class.java)
        .check(classes)
}
```

## Setup

### build.gradle.kts

```kotlin
dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

## Best Practices

1. **Share JavaClasses**: Load classes once in `beforeSpec` to improve performance
2. **Use meaningful test names**: Kotest's string-based test names are great for documentation
3. **Organize with contexts**: Group related tests together
4. **Use data-driven testing**: When testing multiple similar rules
5. **Fail fast**: Architecture tests should run on every build
6. **Document violations**: Use clear package names and test descriptions

## Running Tests

```bash
# Run all tests
./gradlew test

# Run only architecture tests
./gradlew test --tests "*ArchitectureSpec"
./gradlew test --tests "*Spec"

# Run with verbose output
./gradlew test --tests "*ArchitectureSpec" --info
```

## CI/CD Integration

```yaml
# .github/workflows/ci.yml
- name: Run Architecture Tests
  run: ./gradlew test --tests "*Spec"

- name: Upload Test Reports
  if: failure()
  uses: actions/upload-artifact@v3
  with:
    name: architecture-test-reports
    path: build/reports/tests/
```

## Comparison: JUnit5 vs Kotest

| Feature | JUnit5 + ArchUnit | Kotest + ArchUnit |
|---------|-------------------|-------------------|
| Annotations | `@Test`, `@BeforeAll` | No annotations needed |
| Test Styles | One style (method-based) | Multiple styles (FunSpec, BehaviorSpec, etc.) |
| Data-Driven | `@ParameterizedTest` + custom provider | Built-in `withData` |
| Nested Tests | `@Nested` classes | Native `context` blocks |
| Hooks | `@BeforeAll`, `@BeforeEach` | `beforeSpec`, `beforeTest`, etc. |
| Kotlin DSL | No | Yes |
| Test Names | Method names or `@DisplayName` | String literals |

## Conclusion

Kotest + ArchUnit provides a powerful, idiomatic Kotlin way to enforce architecture rules. The combination offers:

- **Better readability** - Natural Kotlin DSL
- **More flexibility** - Multiple test styles
- **Clearer output** - String-based test names
- **Stronger type safety** - Kotlin compiler benefits
- **Better organization** - Nested contexts and data-driven tests

Choose the style that fits your team's preferences and enjoy enforced architectural boundaries!
