package com.github.seijikohara.devtools.webdevtools.repository.api

import com.github.seijikohara.devtools.webdevtools.config.ApplicationProperties
import com.github.seijikohara.devtools.webdevtools.utils.JsonUtils
import kotlinx.serialization.json.JsonElement
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
    suspend fun getGeoByIpAddress(ipAddressString: String): Map<String, JsonElement> =
        "${applicationProperties.network.geo.uri}/$ipAddressString/json"
            .also { uri -> logger.info("[GEO] $uri") }
            .let { uri ->
                webClient
                    .get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .awaitBody<String>()
            }.let(JsonUtils::fromJsonToElements)
}
