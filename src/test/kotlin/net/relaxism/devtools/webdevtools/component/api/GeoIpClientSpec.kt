package net.relaxism.devtools.webdevtools.component.api

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@OptIn(ExperimentalCoroutinesApi::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class GeoIpClientSpec(
    @Autowired private val geoIpClient: GeoIpClient,
) : StringSpec() {

    companion object {
        private val OBJECT_MAPPER = ObjectMapper().findAndRegisterModules()
    }

    init {
        "get : success" {
            runTest {
                mapToJson(
                    geoIpClient.getGeoByIpAddress("0.0.0.0")
                ) shouldBe "{\"status\":\"fail\",\"message\":\"reserved range\",\"query\":\"0.0.0.0\"}"
            }
        }
    }

    private fun mapToJson(map: Map<String, Any?>?): String {
        return OBJECT_MAPPER.writeValueAsString(map)
    }

}
