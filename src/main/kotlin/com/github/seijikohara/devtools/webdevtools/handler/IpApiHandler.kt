package com.github.seijikohara.devtools.webdevtools.handler

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class IpApiHandler {
    suspend fun getIp(request: ServerRequest): ServerResponse =
        Response(
            ipAddress =
                listOf(
                    {
                        request
                            .headers()
                            .firstHeader("X-Forwarded-For")
                            ?.split(",")
                            ?.first()
                            ?.trim()
                    },
                    { request.headers().firstHeader("X-Real-IP")?.trim() },
                    { request.remoteAddress().map { it.address.hostAddress }?.orElse(null) },
                ).asSequence()
                    .mapNotNull { it() }
                    .firstOrNull(),
            hostName = request.remoteAddress().map { it.address.canonicalHostName }?.orElse(null),
        ).let { response ->
            ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(response)
        }

    data class Response(
        val ipAddress: String?,
        val hostName: String?,
    )
}
