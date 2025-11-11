package io.github.seijikohara.devtools.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import io.kotest.core.spec.style.FunSpec

/**
 * Architecture tests using Kotest + ArchUnit.
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
        lateinit var classes: JavaClasses

        beforeSpec {
            classes =
                ClassFileImporter()
                    .withImportOption(ImportOption.DoNotIncludeTests())
                    .importPackages("io.github.seijikohara.devtools")
        }

        context("Clean Architecture layer dependencies") {
            test("domain layer should not depend on infrastructure layer") {
                noClasses()
                    .that()
                    .resideInAPackage("..domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..infrastructure..")
                    .check(classes)
            }

            test("application layer should not depend on infrastructure layer") {
                noClasses()
                    .that()
                    .resideInAPackage("..application..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..infrastructure..")
                    .check(classes)
            }
        }

        context("Domain layer purity") {
            test("domain should not depend on Spring Framework") {
                noClasses()
                    .that()
                    .resideInAPackage("..domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("org.springframework..")
                    .check(classes)
            }

            test("domain should not depend on database libraries") {
                noClasses()
                    .that()
                    .resideInAPackage("..domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("org.springframework.data..", "io.r2dbc..")
                    .check(classes)
            }

            test("domain should not depend on web libraries") {
                noClasses()
                    .that()
                    .resideInAPackage("..domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("org.springframework.web..", "jakarta.servlet..")
                    .check(classes)
            }
        }
    })
