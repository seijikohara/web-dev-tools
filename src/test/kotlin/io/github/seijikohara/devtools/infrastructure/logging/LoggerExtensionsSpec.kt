package io.github.seijikohara.devtools.infrastructure.logging

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.slf4j.Logger

class LoggerExtensionsSpec :
    FunSpec({

        context("Any.logger extension") {
            test("should return Logger for the calling class") {
                val testObject = TestClass()
                val logger = testObject.logger

                logger.name shouldBe TestClass::class.java.name
            }

            test("should return Logger with correct name for anonymous class") {
                val anonymousObject =
                    object {
                        fun getLogger(): Logger = logger
                    }

                val logger = anonymousObject.getLogger()

                // Anonymous classes have names like "LoggerExtensionsSpec$1$1"
                logger.name shouldContain "LoggerExtensionsSpec"
            }

            test("should return different loggers for different classes") {
                val testObject1 = TestClass()
                val testObject2 = AnotherTestClass()

                val logger1 = testObject1.logger
                val logger2 = testObject2.logger

                logger1.name shouldBe TestClass::class.java.name
                logger2.name shouldBe AnotherTestClass::class.java.name
            }

            test("should be accessible from any class") {
                class LocalClass {
                    fun getLoggerName(): String = logger.name
                }

                val localObject = LocalClass()
                val loggerName = localObject.getLoggerName()

                loggerName shouldContain "LocalClass"
            }
        }
    })

private class TestClass

private class AnotherTestClass
