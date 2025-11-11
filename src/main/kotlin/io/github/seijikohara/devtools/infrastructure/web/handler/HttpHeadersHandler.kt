package io.github.seijikohara.devtools.infrastructure.web.handler

import io.github.seijikohara.devtools.application.usecase.GetHttpHeadersUseCase
import io.github.seijikohara.devtools.infrastructure.web.dto.HttpHeadersResponseDto
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

/**
 * Handler function for HTTP headers endpoint.
 *
 * Returns all HTTP headers from the request through the GetHttpHeadersUseCase.
 *
 * @param request The server request containing HTTP headers
 * @param useCase The use case for extracting HTTP headers
 * @return ServerResponse with all request headers
 */
suspend fun handleGetHttpHeaders(
    request: ServerRequest,
    useCase: GetHttpHeadersUseCase,
): ServerResponse =
    ServerResponse
        .ok()
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValueAndAwait(
            useCase(
                GetHttpHeadersUseCase.Request(
                    headers = request.headers().asHttpHeaders().toMap(),
                ),
            ).let { response ->
                HttpHeadersResponseDto(
                    headers =
                        response.headers.map { header ->
                            HttpHeadersResponseDto.HttpHeaderDto(
                                name = header.name,
                                value = header.value,
                            )
                        },
                )
            },
        )
