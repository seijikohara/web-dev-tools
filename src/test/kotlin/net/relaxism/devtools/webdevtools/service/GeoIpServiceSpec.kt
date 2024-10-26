package net.relaxism.devtools.webdevtools.service

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import net.relaxism.devtools.webdevtools.component.api.GeoIpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class GeoIpServiceSpec(
    @MockkBean private val geoIpClient: GeoIpClient,
    @Autowired private val geoIpService: GeoIpService,
) : StringSpec() {
    init {
        "getGeoByIpAddress" {
            val ipAddress = "192.0.2.1"
            val expected = mapOf<String, Any?>("key1" to "value1")

            coEvery {
                geoIpClient.getGeoByIpAddress(ipAddress)
            } returns expected

            geoIpService.getGeoFromIpAddress(ipAddress) shouldBe expected
        }
    }
}
