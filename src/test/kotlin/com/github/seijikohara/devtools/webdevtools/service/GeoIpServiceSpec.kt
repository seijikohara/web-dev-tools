package com.github.seijikohara.devtools.webdevtools.service

import com.github.seijikohara.devtools.webdevtools.repository.api.GeoIpApiRepository
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonPrimitive
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GeoIpServiceSpec(
    private val geoIpService: GeoIpService,
    @MockkBean private val mockGeoIpApiRepository: GeoIpApiRepository,
) : FunSpec({

        test("geoIpService should be properly configured") {
            geoIpService shouldNotBe null
        }

        test("getGeoFromIpAddress should return geo information for valid IP") {
            val mockGeoData = mapOf("country" to JsonPrimitive("US"), "city" to JsonPrimitive("Mountain View"))
            coEvery { mockGeoIpApiRepository.getGeoByIpAddress("8.8.8.8") } returns mockGeoData

            val result = geoIpService.getGeoFromIpAddress("8.8.8.8")

            result shouldNotBe null
            result shouldBe mockGeoData
            result?.get("country") shouldBe JsonPrimitive("US")
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
