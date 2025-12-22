import com.github.gradle.node.npm.task.NpmTask
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    // Kotlin
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.serialization)

    // Spring
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)

    // Build tools
    alias(libs.plugins.node.gradle)
    alias(libs.plugins.spotless)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.project.report)
}

group = "io.github.seijikohara.devtools"
version = "1.0.0"

// Java Toolchain configuration
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // BOMs (Bill of Materials) - Define platform dependencies first
    implementation(platform(libs.kotlinx.coroutines.bom))
    implementation(platform(libs.kotlinx.serialization.bom))

    // Kotlin dependencies
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactive)
    implementation(libs.kotlinx.coroutines.reactor)

    // Spring Boot starters
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.data.r2dbc)
    implementation(libs.spring.boot.starter.jdbc)

    // Additional Spring dependencies
    implementation(libs.springdoc.openapi.starter.webflux.ui)
    implementation(libs.reactor.kotlin.extensions)

    // Database (spring-boot-flyway includes flyway-core and spring-boot-jdbc transitively)
    implementation(libs.spring.boot.flyway)

    // Utility libraries
    implementation(libs.guava)
    implementation(libs.ipaddress)

    // Development only
    developmentOnly(libs.spring.boot.devtools)
    annotationProcessor(libs.spring.boot.configuration.processor)

    // Runtime dependencies
    runtimeOnly(libs.h2)
    runtimeOnly(libs.r2dbc.h2)
}

// Kotlin compiler configuration
kotlin {
    compilerOptions {
        // JSR-305 strict mode for better null-safety with Java interop
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

// Testing configuration using Test Suites
testing {
    suites {
        // Default test suite (unit tests)
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()

            dependencies {
                // Test BOMs for version management
                implementation(platform(libs.kotest.bom))

                // Core test dependencies
                implementation(libs.kotest.runner.junit5)
                implementation(libs.kotest.assertions.core)
                implementation(libs.mockk)
                implementation(libs.konsist)
            }
        }

        // Integration test suite
        val integrationTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()

            dependencies {
                // Depend on main source set
                implementation(project())

                // Test BOMs for version management
                implementation(platform(libs.kotest.bom))
                implementation(platform(libs.kotlinx.serialization.bom))

                // Spring Boot test dependencies
                implementation(libs.spring.boot.starter.test)
                implementation(libs.spring.boot.webflux.test)
                implementation(libs.reactor.test)

                // Kotest dependencies
                implementation(libs.kotest.runner.junit5)
                implementation(libs.kotest.assertions.core)

                // Kotest Spring extension
                implementation(libs.kotest.extensions.spring)

                // Additional test dependencies
                implementation(libs.springmockk)
                implementation(libs.kotlinx.serialization.json)
            }

            // Enforce WebFlux usage (exclude servlet-based dependencies)
            configurations {
                all {
                    exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
                    exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
                }
            }

            // Custom source directories
            sources {
                kotlin {
                    srcDirs("src/it/kotlin")
                }
                resources {
                    srcDirs("src/it/resources")
                }
            }

            // Configure test task execution order
            targets.configureEach {
                testTask.configure {
                    description = "Runs integration tests (infrastructure layer)"
                    shouldRunAfter(tasks.named("test"))
                    // Ensure frontend is built before integration tests
                    // Incremental build configuration ensures this only runs when sources change
                    dependsOn(tasks.named("npmRunBuild"))
                }
            }
        }
    }
}

// Task configuration
tasks {
    check {
        dependsOn(testing.suites.named("integrationTest"))
    }

    // Ensure frontend build runs before resource processing when both tasks are executed
    processResources {
        mustRunAfter("npmRunBuild")
    }

    withType<BootJar>().configureEach {
        archiveFileName = "app.jar"
    }
}

// Spring Boot configuration
springBoot {
    buildInfo()
}

// Code formatting
spotless {
    kotlin {
        ktlint()
    }
    kotlinGradle {
        ktlint()
    }
}

// Node.js plugin configuration
node {
    version = libs.versions.nodejs.get()
    npmVersion = libs.versions.npm.get()
    download = true

    tasks {
        val npmInstallDependencies by registering(NpmTask::class) {
            args = listOf("install")
            workingDir = file("frontend")
        }

        val npmRunBuild by registering(NpmTask::class) {
            dependsOn(npmInstallDependencies)
            args = listOf("run", "build-only", "--", "--outDir", "../src/main/resources/static")
            workingDir = file("frontend")
        }
    }
}
