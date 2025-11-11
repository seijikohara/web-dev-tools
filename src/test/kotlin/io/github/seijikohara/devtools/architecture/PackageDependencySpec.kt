package io.github.seijikohara.devtools.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData

/**
 * Package dependency rules using Kotest's data-driven testing.
 *
 * Demonstrates Kotest's powerful data-driven testing with ArchUnit.
 * Each forbidden dependency is tested individually with clear test names.
 */
class PackageDependencySpec :
    DescribeSpec({
        val classes: JavaClasses =
            ClassFileImporter()
                .withImportOption(ImportOption.DoNotIncludeTests())
                .importPackages("io.github.seijikohara.devtools")

        describe("Domain layer forbidden dependencies") {
            data class ForbiddenDependency(
                val name: String,
                val packages: List<String>,
            )

            withData(
                nameFn = { it.name },
                ForbiddenDependency(
                    name = "Spring Framework",
                    packages = listOf("org.springframework.."),
                ),
                ForbiddenDependency(
                    name = "Spring Data",
                    packages = listOf("org.springframework.data.."),
                ),
                ForbiddenDependency(
                    name = "R2DBC",
                    packages = listOf("io.r2dbc.."),
                ),
                ForbiddenDependency(
                    name = "Spring Web",
                    packages = listOf("org.springframework.web.."),
                ),
                ForbiddenDependency(
                    name = "Jakarta Servlet",
                    packages = listOf("jakarta.servlet.."),
                ),
                ForbiddenDependency(
                    name = "Infrastructure layer",
                    packages = listOf("..infrastructure.."),
                ),
            ) { (_, packages) ->
                noClasses()
                    .that()
                    .resideInAPackage("..domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(*packages.toTypedArray())
                    .check(classes)
            }
        }

        describe("Application layer forbidden dependencies") {
            data class ForbiddenDependency(
                val name: String,
                val packages: List<String>,
            )

            withData(
                nameFn = { it.name },
                ForbiddenDependency(
                    name = "Infrastructure layer",
                    packages = listOf("..infrastructure.."),
                ),
                ForbiddenDependency(
                    name = "Database libraries",
                    packages = listOf("org.springframework.data..", "io.r2dbc.."),
                ),
                ForbiddenDependency(
                    name = "Web libraries",
                    packages = listOf("org.springframework.web..", "jakarta.servlet.."),
                ),
            ) { (_, packages) ->
                noClasses()
                    .that()
                    .resideInAPackage("..application..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(*packages.toTypedArray())
                    .check(classes)
            }
        }
    })
