package net.relaxism.devtools.webdevtools.handler

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class HttpHeadersApiHandler {
    suspend fun getHttpHeaders(request: ServerRequest): ServerResponse =
        request
            .headers()
            .asHttpHeaders()
            .keys
            .flatMap { headerName ->
                request
                    .headers()
                    .header(headerName)
                    .map { headerValue -> Response.Header(headerName, headerValue) }
            }.let { headers ->
                ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValueAndAwait(Response(headers))
            }

    data class Response(
        val headers: List<Header>,
    ) {
        data class Header(
            val name: String,
            val value: String,
        )
    }
}
