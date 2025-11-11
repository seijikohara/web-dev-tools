package io.github.seijikohara.devtools.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import io.kotest.core.spec.style.FunSpec

/**
 * Naming convention rules using Kotest's FunSpec.
 *
 * Enforces consistent naming patterns across the codebase using
 * simple and readable test structure.
 */
class NamingConventionSpec :
    FunSpec({
        lateinit var classes: JavaClasses

        beforeSpec {
            classes =
                ClassFileImporter()
                    .withImportOption(ImportOption.DoNotIncludeTests())
                    .importPackages("io.github.seijikohara.devtools")
        }

        context("Domain layer naming conventions") {
            test("repository interfaces should end with 'Repository'") {
                classes()
                    .that()
                    .resideInAPackage("..domain..repository..")
                    .and()
                    .areInterfaces()
                    .should()
                    .haveSimpleNameEndingWith("Repository")
                    .check(classes)
            }
        }

        context("Application layer naming conventions") {
            test("use case functional interfaces should end with 'UseCase'") {
                classes()
                    .that()
                    .resideInAPackage("..application.usecase..")
                    .and()
                    .areInterfaces()
                    .and()
                    .haveSimpleNameEndingWith("UseCase")
                    .should()
                    .beInterfaces()
                    .check(classes)
            }
        }

        context("Infrastructure layer naming conventions") {
            test("repository implementations should end with 'Adapter'") {
                classes()
                    .that()
                    .resideInAPackage("..infrastructure..")
                    .and()
                    .haveSimpleNameContaining("Repository")
                    .and()
                    .areNotInterfaces()
                    .should()
                    .haveSimpleNameEndingWith("Adapter")
                    .check(classes)
            }
        }

        context("Configuration class naming conventions") {
            test("configuration classes should end with 'Configuration' or 'Config'") {
                classes()
                    .that()
                    .areAnnotatedWith(org.springframework.context.annotation.Configuration::class.java)
                    .should()
                    .haveSimpleNameEndingWith("Configuration")
                    .orShould()
                    .haveSimpleNameEndingWith("Config")
                    .check(classes)
            }
        }
    })
