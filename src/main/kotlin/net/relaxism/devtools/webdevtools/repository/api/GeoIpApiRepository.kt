package net.relaxism.devtools.webdevtools.repository.api

import kotlinx.coroutines.coroutineScope
import net.relaxism.devtools.webdevtools.config.ApplicationProperties
import net.relaxism.devtools.webdevtools.utils.JsonUtils
import org.slf4j.Logger
import org.springframework.http.MediaType
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Repository
class GeoIpApiRepository(
    private val logger: Logger,
    private val applicationProperties: ApplicationProperties,
    private val webClient: WebClient,
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
