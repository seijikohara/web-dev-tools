package net.relaxism.devtools.webdevtools.service

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import kotlinx.coroutines.runBlocking
import net.relaxism.devtools.webdevtools.repository.api.RdapApiRepository
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RdapServiceSpec(
    private val rdapService: RdapService,
    @MockkBean private val mockRdapApiRepository: RdapApiRepository,
) : FunSpec({

        test("rdapService should be properly configured") {
            rdapService shouldNotBe null
        }

        test("getRdapByIpAddress should return rdap information for valid IP") {
            val mockRdapData = mapOf("handle" to "8.8.8.8", "country" to "US")
            coEvery { mockRdapApiRepository.getRdapByIpAddress("8.8.8.8") } returns mockRdapData

            val result = rdapService.getRdapByIpAddress("8.8.8.8")

            result shouldNotBe null
            result shouldBe mockRdapData
            result["handle"] shouldBe "8.8.8.8"
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
