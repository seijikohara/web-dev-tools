package net.relaxism.devtools.webdevtools.handler

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class HttpHeadersApiHandler {

    fun getHttpHeaders(request: ServerRequest): Mono<ServerResponse> {
        val headerNames = request.headers().asHttpHeaders().keys
        val headers = headerNames.flatMap { headerName ->
            val headerValues = request.headers().header(headerName)
            headerValues.map { headerValue -> Response.Header(headerName, headerValue) }
        }

        val response = Response(headers)
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(response), Response::class.java)
    }

    data class Response(val headers: List<Header>) {
        data class Header(val name: String, val value: String)
    }

}
