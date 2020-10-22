package net.relaxism.devtools.webdevtools.handler

import net.relaxism.devtools.webdevtools.config.ApplicationProperties
import net.relaxism.devtools.webdevtools.utils.JsonUtils
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class GeoApiHandler(
    @Autowired val logger: Logger,
    @Autowired val applicationProperties: ApplicationProperties,
    @Autowired val restTemplate: RestTemplate
) : ExternalApiCaller {

    fun getGeo(request: ServerRequest): Mono<ServerResponse> {
        val ipAddress = request.pathVariable("ip")
        val uri = "${applicationProperties.network.geo.uri}/${ipAddress}";
        logger.info("[GEO] ${uri}")
        val geoJson = callExternalApi(restTemplate, uri)

        val response = Response(JsonUtils.fromJson(geoJson))
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(response), Response::class.java)
    }

    data class Response(val geo: Map<String?, Any?>?)

}
