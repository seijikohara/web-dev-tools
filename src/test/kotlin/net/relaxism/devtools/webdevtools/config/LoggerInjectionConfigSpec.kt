package net.relaxism.devtools.webdevtools.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.slf4j.Logger
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class LoggerInjectionConfigSpec(
    private val logger: Logger,
) : FunSpec({

        test("logger should be properly injected") {
            logger shouldNotBe null
            logger.name shouldBe LoggerInjectionConfigSpec::class.java.name
        }

        test("logger should be functional") {
            logger.isInfoEnabled shouldBe true
            logger.info("Test log message from LoggerInjectionConfigSpec")
        }
    })
