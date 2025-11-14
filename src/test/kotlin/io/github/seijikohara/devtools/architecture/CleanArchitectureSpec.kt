package io.github.seijikohara.devtools.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertFalse
import io.kotest.core.spec.style.FunSpec

/**
 * Architecture tests using Kotest + Konsist.
 *
 * Verifies Clean Architecture and DDD principles:
 * - Domain layer has no dependencies on infrastructure concerns
 * - Application layer depends only on domain
 * - Infrastructure layer can depend on application and domain
 *
 * Uses Kotest's FunSpec style for idiomatic Kotlin testing.
 */
class CleanArchitectureSpec :
    FunSpec({
        context("Clean Architecture layer dependencies") {
            test("domain layer should not depend on infrastructure layer") {
                Konsist
                    .scopeFromProduction()
                    .files
                    .withPackage("..domain..")
                    .assertFalse(testName = koTestName) {
                        it.hasImport { import -> import.name.contains(".infrastructure.") }
                    }
            }

            test("application layer should not depend on infrastructure layer") {
                Konsist
                    .scopeFromProduction()
                    .files
                    .withPackage("..application..")
                    .assertFalse(testName = koTestName) {
                        it.hasImport { import -> import.name.contains(".infrastructure.") }
                    }
            }
        }

        context("Domain layer purity") {
            test("domain should not depend on Spring Framework") {
                Konsist
                    .scopeFromProduction()
                    .files
                    .withPackage("..domain..")
                    .assertFalse(testName = koTestName) {
                        it.hasImport { import -> import.name.startsWith("org.springframework") }
                    }
            }

            test("domain should not depend on database libraries") {
                Konsist
                    .scopeFromProduction()
                    .files
                    .withPackage("..domain..")
                    .assertFalse(testName = koTestName) {
                        it.hasImport { import ->
                            import.name.startsWith("org.springframework.data") ||
                                import.name.startsWith("io.r2dbc")
                        }
                    }
            }

            test("domain should not depend on web libraries") {
                Konsist
                    .scopeFromProduction()
                    .files
                    .withPackage("..domain..")
                    .assertFalse(testName = koTestName) {
                        it.hasImport { import ->
                            import.name.startsWith("org.springframework.web") ||
                                import.name.startsWith("jakarta.servlet")
                        }
                    }
            }
        }
    })
