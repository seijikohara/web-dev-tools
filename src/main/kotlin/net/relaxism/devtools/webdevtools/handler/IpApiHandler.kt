package net.relaxism.devtools.webdevtools.handler

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class IpApiHandler {

    fun getIp(request: ServerRequest): Mono<ServerResponse> {
        val ipAddress = getRealRemoteIpAddress(request)
        val ipResponse = IpResponse(ipAddress)
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(ipResponse), IpResponse::class.java)
    }

    private fun getRealRemoteIpAddress(request: ServerRequest): String {
        val xForwardedFor: String? = request.headers().firstHeader("X-Forwarded-For")
        val remoteAddress = request.remoteAddress();
        return xForwardedFor ?: remoteAddress
            .map { inetSocketAddress -> inetSocketAddress.address.toString() }
            .orElse(null)
    }

    data class IpResponse(val ipAddress: String?)

}
