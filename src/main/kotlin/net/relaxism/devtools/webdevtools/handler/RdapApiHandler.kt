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
class RdapApiHandler(
    @Autowired val logger: Logger,
    @Autowired val applicationProperties: ApplicationProperties,
    @Autowired val restTemplate: RestTemplate
) : ExternalApiCaller {

    fun getRdap(request: ServerRequest): Mono<ServerResponse> {
        val ipAddress = request.pathVariable("ip")
        val uri = "${applicationProperties.network.rdap.uri}/${ipAddress}";
        logger.info("[RDAP] ${uri}")
        val rdapJson = callExternalApi(restTemplate, uri)

        val response = Response(JsonUtils.fromJson(rdapJson))
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(response), Response::class.java)
    }

    data class Response(val rdap: Map<String?, Any?>?)

}
