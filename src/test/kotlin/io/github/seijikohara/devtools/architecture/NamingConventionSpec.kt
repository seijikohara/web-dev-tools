package io.github.seijikohara.devtools.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withAllAnnotationsOf
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertTrue
import io.kotest.core.spec.style.FunSpec

/**
 * Naming convention rules using Kotest's FunSpec.
 *
 * Enforces consistent naming patterns across the codebase using
 * simple and readable test structure.
 */
class NamingConventionSpec :
    FunSpec({
        context("Interface naming conventions") {
            test("repository interfaces should end with 'Repository' or 'Resolver'") {
                Konsist
                    .scopeFromProduction()
                    .interfaces()
                    .withPackage("..domain..repository..")
                    .assertTrue(testName = koTestName) {
                        it.name.endsWith("Repository") || it.name.endsWith("Resolver")
                    }
            }

            test("use case functional interfaces should end with 'UseCase'") {
                Konsist
                    .scopeFromProduction()
                    .interfaces()
                    .withPackage("..application.usecase..")
                    .assertTrue(testName = koTestName) {
                        it.name.endsWith("UseCase")
                    }
            }
        }

        context("Implementation naming conventions") {
            test("repository implementations should end with 'Adapter'") {
                Konsist
                    .scopeFromProduction()
                    .classes()
                    .filter { klass ->
                        val packageName = klass.packagee?.name ?: ""
                        (
                            packageName.contains(".infrastructure.database.") ||
                                packageName.contains(".infrastructure.externalapi.")
                        ) &&
                            klass.name.contains("Repository")
                    }.assertTrue(testName = koTestName) {
                        it.name.endsWith("Adapter")
                    }
            }

            test("configuration classes should end with 'Configuration' or 'Config'") {
                Konsist
                    .scopeFromProduction()
                    .classes()
                    .withAllAnnotationsOf(org.springframework.context.annotation.Configuration::class)
                    .assertTrue(testName = koTestName) {
                        it.name.endsWith("Configuration") || it.name.endsWith("Config")
                    }
            }
        }
    })
