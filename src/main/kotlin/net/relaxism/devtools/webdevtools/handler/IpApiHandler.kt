package net.relaxism.devtools.webdevtools.handler

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class IpApiHandler() {

    fun getIp(request: ServerRequest): Mono<ServerResponse> {
        val remoteAddress = request.remoteAddress()
        val remoteIpAddress = request.headers().firstHeader("X-Forwarded-For")
            ?: remoteAddress.map { it.address.hostAddress }.orElse(null)
        val remoteHostname = remoteAddress.map { it.address.canonicalHostName }.orElse(null)

        val response = Response(remoteIpAddress, remoteHostname)
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(response), Response::class.java)
    }

    data class Response(val ipAddress: String?, val hostName: String?)

}
