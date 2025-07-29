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
    alias(libs.plugins.versions)
    alias(libs.plugins.project.report)
}

group = "com.github.seijikohara.devtools"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
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

    // Testing
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.reactor.test)
    testImplementation(libs.springmockk)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.assertions.json)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotest.extensions.spring)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.coroutines.debug)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
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

/**
 * Heroku
 */

val herokuStageBuildFrontend by tasks.registering {
    group = "heroku"
    dependsOn(npmRunBuild)
    doLast {
        delete("frontend/node_modules")
    }
}

val herokuStageBuild by tasks.registering {
    group = "heroku"
    dependsOn("bootJar")
    mustRunAfter(herokuStageBuildFrontend)
}

val herokuStage by tasks.registering {
    group = "heroku"
    dependsOn(herokuStageBuild)
    dependsOn(herokuStageBuildFrontend)
}
