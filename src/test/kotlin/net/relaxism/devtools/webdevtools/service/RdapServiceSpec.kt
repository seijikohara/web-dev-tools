package net.relaxism.devtools.webdevtools.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.runBlocking
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RdapServiceSpec(
    private val rdapService: RdapService,
) : FunSpec({

        test("rdapService should be properly configured") {
            rdapService shouldNotBe null
        }

        test("getRdapByIpAddress should handle invalid inputs") {
            forAll(
                row("", "empty string"),
                row("   ", "whitespace only"),
                row("\t\n", "tab and newline"),
            ) { ip, description ->
                var exceptionThrown = false
                try {
                    runBlocking {
                        rdapService.getRdapByIpAddress(ip)
                    }
                } catch (e: IllegalArgumentException) {
                    exceptionThrown = true
                }
                exceptionThrown shouldBe true
            }
        }
    })
