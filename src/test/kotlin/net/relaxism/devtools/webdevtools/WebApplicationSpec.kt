package net.relaxism.devtools.webdevtools

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest
class WebApplicationSpec(
    private val applicationContext: ApplicationContext,
) : FunSpec({

        test("application context should load successfully") {
            applicationContext shouldNotBe null
        }

        test("main function should not throw exception") {
            // Test that main function can be called without throwing exception
            main(arrayOf())
        }
    })
