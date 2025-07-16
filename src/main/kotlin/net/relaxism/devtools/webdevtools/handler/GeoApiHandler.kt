package net.relaxism.devtools.webdevtools.handler

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
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
    suspend fun getGeo(request: ServerRequest): ServerResponse =
        request
            .pathVariable("ip")
            .let { ipAddress -> geoIpService.getGeoFromIpAddress(ipAddress) }
            .let { clientResponse ->
                ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValueAndAwait(Response(geo = clientResponse))
            }

    @Serializable
    data class Response(
        val geo: Map<String, JsonElement>?,
    )
}
