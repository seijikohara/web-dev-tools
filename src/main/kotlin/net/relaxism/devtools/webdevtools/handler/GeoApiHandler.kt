package net.relaxism.devtools.webdevtools.handler

import net.relaxism.devtools.webdevtools.service.GeoIpService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class GeoApiHandler(
    @Autowired private val geoIpService: GeoIpService
) {

    fun getGeo(request: ServerRequest): Mono<ServerResponse> {
        val ipAddress = request.pathVariable("ip")
        val clientResponseMono = geoIpService.getGeoFromIpAddress(ipAddress)

        return clientResponseMono.flatMap { json ->
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(Response(json)), Response::class.java)
        }
    }

    data class Response(val geo: Map<String, Any?>?)

}
