package io.github.seijikohara.devtools.infrastructure.web.handler

import io.github.seijikohara.devtools.infrastructure.web.dto.HttpHeadersResponseDto
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

/**
 * Handler function for HTTP headers endpoint.
 *
 * Returns all HTTP headers from the request. This is useful for debugging
 * and diagnosing HTTP request issues.
 *
 * @param request The server request containing HTTP headers
 * @return ServerResponse with all request headers
 */
suspend fun handleGetHttpHeaders(request: ServerRequest): ServerResponse =
    ServerResponse
        .ok()
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValueAndAwait(
            HttpHeadersResponseDto(
                request
                    .headers()
                    .asHttpHeaders()
                    .flatMap { (name, values) ->
                        values.map { value ->
                            HttpHeadersResponseDto.HttpHeaderDto(
                                name = name,
                                value = value,
                            )
                        }
                    },
            ),
        )
