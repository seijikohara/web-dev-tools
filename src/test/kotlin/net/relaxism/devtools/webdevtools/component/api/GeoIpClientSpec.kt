package net.relaxism.devtools.webdevtools.component.api

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class GeoIpClientSpec(
    @Autowired private val geoIpClient: GeoIpClient,
) : StringSpec() {

    private val objectMapper = ObjectMapper()

    init {

        "get : success" {
            @Suppress("BlockingMethodInNonBlockingContext")
            mapToJson(
                geoIpClient.getGeoByIpAddress("0.0.0.0").block()
            ) shouldBe "{\"status\":\"fail\",\"message\":\"reserved range\",\"query\":\"0.0.0.0\"}"
        }

    }

    private fun mapToJson(map: Map<String, Any?>?): String {
        return objectMapper.writeValueAsString(map)
    }

}
