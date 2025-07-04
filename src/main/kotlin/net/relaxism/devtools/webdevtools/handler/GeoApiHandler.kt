package net.relaxism.devtools.webdevtools.handler

import net.relaxism.devtools.webdevtools.service.GeoIpService
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class GeoApiHandler(
    private val geoIpService: GeoIpService,
) {
    suspend fun getGeo(request: ServerRequest): ServerResponse {
        val ipAddress = request.pathVariable("ip")
        val clientResponse = geoIpService.getGeoFromIpAddress(ipAddress)

        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(Response(geo = clientResponse))
    }

    data class Response(
        val geo: Map<String, Any?>?,
    )
}
