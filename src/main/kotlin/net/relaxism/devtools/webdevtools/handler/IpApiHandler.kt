package net.relaxism.devtools.webdevtools.handler

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class IpApiHandler {
    suspend fun getIp(request: ServerRequest): ServerResponse {
        val remoteAddress = request.remoteAddress()

        // Use scope functions and Elvis operator for cleaner extraction
        val remoteIpAddress =
            request
                .headers()
                .firstHeader("X-Forwarded-For")
                ?.split(",")
                ?.first()
                ?.trim()
                ?: request
                    .headers()
                    .firstHeader("X-Real-IP")
                    ?.trim()
                ?: remoteAddress.map { it.address.hostAddress }.orElse(null)

        val remoteHostname = remoteAddress.map { it.address.canonicalHostName }.orElse(null)

        // Use expression body for response creation
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(Response(remoteIpAddress, remoteHostname))
    }

    data class Response(
        val ipAddress: String?,
        val hostName: String?,
    )
}
