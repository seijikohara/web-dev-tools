package net.relaxism.devtools.webdevtools.repository.api

import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.core.spec.style.StringSpec
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class GeoIpApiRepositorySpec(
    private val geoIpApiRepository: GeoIpApiRepository,
) : StringSpec() {
    init {
        "get : success" {
            runTest {
                JSONObject(geoIpApiRepository.getGeoByIpAddress("1.1.1.1")).toString() shouldContainJsonKey "$.ip"
            }
        }
    }
}
