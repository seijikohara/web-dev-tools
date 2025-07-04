package net.relaxism.devtools.webdevtools.service

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import net.relaxism.devtools.webdevtools.repository.api.RdapApiRepository
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class RdapServiceSpec(
    @MockkBean private val rdapApiRepository: RdapApiRepository,
    private val rdapService: RdapService,
) : StringSpec() {
    init {
        "getRdapByIpAddress" {
            val ipAddress = "192.0.2.1"
            val expected = mapOf<String, Any?>("key1" to "value1")

            coEvery {
                rdapApiRepository.getRdapByIpAddress(ipAddress)
            } returns expected

            rdapService.getRdapByIpAddress(ipAddress) shouldBe expected
        }
    }
}
