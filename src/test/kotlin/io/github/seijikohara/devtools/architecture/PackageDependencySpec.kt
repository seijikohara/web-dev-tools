package io.github.seijikohara.devtools.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertFalse
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData

/**
 * Package dependency rules using Kotest's data-driven testing.
 *
 * Demonstrates Kotest's powerful data-driven testing with Konsist.
 * Each forbidden dependency is tested individually with clear test names.
 */
class PackageDependencySpec :
    FunSpec({
        context("Domain layer forbidden dependencies") {
            data class ForbiddenDependency(
                val name: String,
                val packagePrefixes: List<String>,
            )

            withData(
                nameFn = { it.name },
                ForbiddenDependency(
                    name = "Spring Framework",
                    packagePrefixes = listOf("org.springframework"),
                ),
                ForbiddenDependency(
                    name = "Spring Data",
                    packagePrefixes = listOf("org.springframework.data"),
                ),
                ForbiddenDependency(
                    name = "R2DBC",
                    packagePrefixes = listOf("io.r2dbc"),
                ),
                ForbiddenDependency(
                    name = "Spring Web",
                    packagePrefixes = listOf("org.springframework.web"),
                ),
                ForbiddenDependency(
                    name = "Jakarta Servlet",
                    packagePrefixes = listOf("jakarta.servlet"),
                ),
                ForbiddenDependency(
                    name = "Infrastructure layer",
                    packagePrefixes = listOf(".infrastructure."),
                ),
            ) { (_, packagePrefixes) ->
                Konsist
                    .scopeFromProduction()
                    .files
                    .withPackage("..domain..")
                    .assertFalse(testName = koTestName) {
                        it.hasImport { import ->
                            packagePrefixes.any { prefix ->
                                if (prefix.startsWith(".")) {
                                    import.name.contains(prefix)
                                } else {
                                    import.name.startsWith(prefix)
                                }
                            }
                        }
                    }
            }
        }

        context("Application layer forbidden dependencies") {
            data class ForbiddenDependency(
                val name: String,
                val packagePrefixes: List<String>,
            )

            withData(
                nameFn = { it.name },
                ForbiddenDependency(
                    name = "Infrastructure layer",
                    packagePrefixes = listOf(".infrastructure."),
                ),
                ForbiddenDependency(
                    name = "Database libraries",
                    packagePrefixes = listOf("org.springframework.data", "io.r2dbc"),
                ),
                ForbiddenDependency(
                    name = "Web libraries",
                    packagePrefixes = listOf("org.springframework.web", "jakarta.servlet"),
                ),
            ) { (_, packagePrefixes) ->
                Konsist
                    .scopeFromProduction()
                    .files
                    .withPackage("..application..")
                    .assertFalse(testName = koTestName) {
                        it.hasImport { import ->
                            packagePrefixes.any { prefix ->
                                if (prefix.startsWith(".")) {
                                    import.name.contains(prefix)
                                } else {
                                    import.name.startsWith(prefix)
                                }
                            }
                        }
                    }
            }
        }
    })
