package net.relaxism.devtools.webdevtools.component.api

import net.relaxism.devtools.webdevtools.config.ApplicationProperties
import net.relaxism.devtools.webdevtools.utils.JsonUtils
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class GeoIpClient(
    @Autowired private val logger: Logger,
    @Autowired private val applicationProperties: ApplicationProperties,
    @Autowired private val webClient: WebClient
) {

    fun getGeoByIpAddress(ipAddressString: String): Mono<Map<String, Any?>> {
        val uri = "${applicationProperties.network.geo.uri}/${ipAddressString}"
        logger.info("[GEO] $uri")
        return webClient.get()
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToMono { response ->
                response.bodyToMono(String::class.java)
                    .map { responseBody -> JsonUtils.fromJson(responseBody) }
                    .switchIfEmpty(Mono.defer { Mono.just(mapOf()) })
            }
    }

}
