import com.github.gradle.node.npm.task.NpmTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("project-report")
    id("com.github.node-gradle.node") version "7.0.1"
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.spring") version "1.9.21"
    id("com.github.ben-manes.versions") version "0.50.0"
}

group = "net.relaxism.devtools"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_21

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val kotlinxCoroutinesVersion = "1.7.3"
val kotestVersion = "5.8.0"

dependencies {
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.github.seancfoley:ipaddress:5.4.0")
    implementation("com.google.guava:guava:32.1.3-jre")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${kotlinxCoroutinesVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${kotlinxCoroutinesVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:${kotlinxCoroutinesVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${kotlinxCoroutinesVersion}")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("io.r2dbc:r2dbc-h2")

    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
    testImplementation("io.kotest:kotest-assertions-core:${kotestVersion}")
    testImplementation("io.kotest:kotest-assertions-json:${kotestVersion}")
    testImplementation("io.kotest:kotest-property:${kotestVersion}")
    testImplementation("io.kotest:kotest-runner-junit5:${kotestVersion}")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:${kotlinxCoroutinesVersion}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${kotlinxCoroutinesVersion}")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<BootJar> {
    archiveFileName.set("app.jar")
}

/**
 * Node.js
 */

node {
    version.set("20.9.0")
    npmVersion.set("10.2.3")
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
