package net.relaxism.devtools.webdevtools.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.runBlocking
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GeoIpServiceSpec(
    private val geoIpService: GeoIpService,
) : FunSpec({

        test("geoIpService should be properly configured") {
            geoIpService shouldNotBe null
        }

        test("getGeoFromIpAddress should return geo information for valid IP") {
            val result = geoIpService.getGeoFromIpAddress("8.8.8.8")

            result shouldNotBe null
            result.keys.isNotEmpty() shouldNotBe false
        }

        test("getGeoFromIpAddress should handle invalid inputs") {
            forAll(
                row("", "empty string"),
                row("   ", "whitespace only"),
                row("\t\n", "tab and newline"),
            ) { ip, description ->
                var exceptionThrown = false
                try {
                    runBlocking {
                        geoIpService.getGeoFromIpAddress(ip)
                    }
                } catch (e: IllegalArgumentException) {
                    exceptionThrown = true
                }
                exceptionThrown shouldBe true
            }
        }
    })
