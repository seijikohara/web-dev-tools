package net.relaxism.devtools.webdevtools.handler

import net.relaxism.devtools.webdevtools.config.ApplicationProperties
import net.relaxism.devtools.webdevtools.service.ExternalJsonApiService
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI

@Component
class RdapApiHandler(
    @Autowired private val logger: Logger,
    @Autowired private val applicationProperties: ApplicationProperties,
    @Autowired private val externalJsonApiService: ExternalJsonApiService
) {

    fun getRdap(request: ServerRequest): Mono<ServerResponse> {
        val ipAddress = request.pathVariable("ip")
        val uri = "${applicationProperties.network.rdap.uri}/${ipAddress}";
        logger.info("[RDAP] ${uri}")
        val clientResponseMono = externalJsonApiService.get(URI.create(uri))

        return clientResponseMono.flatMap { json ->
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(Response(json)), Response::class.java)
        }
    }

    data class Response(val rdap: Map<String?, Any?>?)

}
