package net.relaxism.devtools.webdevtools.component.api

import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.core.spec.style.StringSpec
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@OptIn(ExperimentalCoroutinesApi::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class GeoIpClientSpec(
    @Autowired private val geoIpClient: GeoIpClient,
) : StringSpec() {

    init {
        "get : success" {
            runTest {
                JSONObject(geoIpClient.getGeoByIpAddress("1.1.1.1")).toString() shouldContainJsonKey "$.status"
            }
        }
    }

}
