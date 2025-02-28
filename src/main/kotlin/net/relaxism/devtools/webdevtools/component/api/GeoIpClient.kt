package net.relaxism.devtools.webdevtools.component.api

import kotlinx.coroutines.coroutineScope
import net.relaxism.devtools.webdevtools.config.ApplicationProperties
import net.relaxism.devtools.webdevtools.utils.JsonUtils
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class GeoIpClient(
    @Autowired private val logger: Logger,
    @Autowired private val applicationProperties: ApplicationProperties,
    @Autowired private val webClient: WebClient,
) {
    suspend fun getGeoByIpAddress(ipAddressString: String): Map<String, Any?> =
        coroutineScope {
            val uri = "${applicationProperties.network.geo.uri}/$ipAddressString/json"
            logger.info("[GEO] $uri")

            val jsonString =
                webClient
                    .get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .awaitBody<String>()
            JsonUtils.fromJson(jsonString)
        }
}
