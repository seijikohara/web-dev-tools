import com.github.gradle.node.npm.task.NpmTask
import org.gradle.api.plugins.JavaPlugin
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

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // BOMs (Bill of Materials)
    implementation(platform(libs.kotlinx.coroutines.bom))
    implementation(platform(libs.kotlinx.serialization.bom))

    // Kotlin
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactive)
    implementation(libs.kotlinx.coroutines.reactor)

    // Spring Boot
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.data.r2dbc)
    implementation(libs.spring.boot.starter.jdbc)

    // Reactor
    implementation(libs.reactor.kotlin.extensions)

    // Database
    implementation(libs.flyway.core)

    // Libraries
    implementation(libs.guava)
    implementation(libs.ipaddress)

    // Development
    developmentOnly(libs.spring.boot.devtools)
    annotationProcessor(libs.spring.boot.configuration.processor)

    // Runtime
    runtimeOnly(libs.h2)
    runtimeOnly(libs.r2dbc.h2)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()

            dependencies {
                implementation(platform(libs.kotest.bom))
                implementation(libs.kotest.runner.junit5)
                implementation(libs.kotest.assertions.core)
                implementation(libs.mockk)
            }
        }

        val integrationTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()

            dependencies {
                implementation(project())
                implementation(platform(libs.kotest.bom))
                implementation(platform(libs.kotlinx.serialization.bom))
                implementation(libs.spring.boot.starter.test)
                implementation(libs.spring.boot.starter.webflux)
                implementation(libs.reactor.test)
                implementation(libs.kotest.runner.junit5)
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotest.extensions.spring) {
                    exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
                }
                implementation(libs.springmockk)
                implementation(libs.kotlinx.serialization.json)
            }

            // Fix for JvmTestSuite not inheriting main implementation dependencies
            // See: https://github.com/gradle/gradle/issues/25269
            configurations {
                named(sources.implementationConfigurationName) {
                    extendsFrom(project.configurations.getByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME))
                }

                // Exclude spring-boot-starter-web from all configurations to force WebFlux usage
                all {
                    exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
                    exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
                }
            }

            sources {
                kotlin {
                    srcDirs("src/it/kotlin")
                }
                resources {
                    srcDirs("src/it/resources")
                }
            }

            targets {
                all {
                    testTask.configure {
                        description = "Runs integration tests (infrastructure layer)"
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("test"))
}

tasks.withType<BootJar> {
    archiveFileName.set("app.jar")
}

spotless {
    kotlin {
        ktlint()
    }
    kotlinGradle {
        ktlint()
    }
}

/**
 * Node.js
 */

node {
    version.set(libs.versions.nodejs.get())
    npmVersion.set(libs.versions.npm.get())
    download.set(true)
}

// Task for installing frontend dependencies in web
val npmInstallDependencies by tasks.registering(NpmTask::class) {
    args.set(listOf("install"))
    workingDir.set(File("./frontend"))
}

// Task for executing build:gradle in web
val npmRunBuild by tasks.registering(NpmTask::class) {
    // Before buildWeb can run, installDependencies must run
    dependsOn(npmInstallDependencies)

    args.set(listOf("run", "build-only", "--", "--outDir", "../src/main/resources/static"))
    workingDir.set(File("./frontend"))
}
