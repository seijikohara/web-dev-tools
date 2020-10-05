import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.moowork.gradle.node.npm.NpmTask

plugins {
    id("com.github.node-gradle.node") version "2.2.4"
    id("org.springframework.boot") version "2.3.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
}

group = "net.relaxism.devtools"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}


/**
 * Node.js
 */

node {
    version = "12.16.1"
    npmVersion = "6.14.2"
    download = true
}

// Task for installing frontend dependencies in web
val npmInstallDependencies by tasks.registering(NpmTask::class) {
    setArgs(listOf("install"))
    setExecOverrides(closureOf<ExecSpec> {
        setWorkingDir("./client")
    })
}

// Task for executing build:gradle in web
val npmRunBuild by tasks.registering(NpmTask::class) {
    // Before buildWeb can run, installDependencies must run
    dependsOn(npmInstallDependencies)

    setArgs(listOf("run", "build", "--", "--dest", "../src/main/resources/generated"))
    setExecOverrides(closureOf<ExecSpec> {
        setWorkingDir("./client")
    })
}


/**
 * Heroku
 */

val herokuStageBuildClient by tasks.registering {
    group = "heroku"
    dependsOn(npmRunBuild)
    doLast {
        delete("client/node_modules")
    }
}

val herokuStageBuild by tasks.registering {
    group = "heroku"
    dependsOn("bootJar")
    mustRunAfter(herokuStageBuildClient)
}

val herokuStage by tasks.registering {
    group = "heroku"
    dependsOn(herokuStageBuild)
    dependsOn(herokuStageBuildClient)
}
