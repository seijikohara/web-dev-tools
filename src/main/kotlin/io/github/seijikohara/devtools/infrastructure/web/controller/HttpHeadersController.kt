package io.github.seijikohara.devtools.infrastructure.web.controller

import io.github.seijikohara.devtools.application.usecase.GetHttpHeadersUseCase
import io.github.seijikohara.devtools.infrastructure.web.dto.HttpHeadersResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for HTTP headers endpoint.
 */
@RestController
@RequestMapping("\${application.api-base-path}")
@Tag(name = "Request Information")
class HttpHeadersController(
    private val getHttpHeadersUseCase: GetHttpHeadersUseCase,
) {
    /**
     * Retrieves HTTP request headers.
     *
     * @param request Server HTTP request
     * @return [HttpHeadersResponseDto] instance
     */
    @GetMapping("/http-headers")
    @Operation(summary = "Get HTTP request headers")
    suspend fun getHttpHeaders(request: ServerHttpRequest): HttpHeadersResponseDto =
        getHttpHeadersUseCase(
            GetHttpHeadersUseCase.Request(
                headers = request.headers.toMap(),
            ),
        ).let { response ->
            HttpHeadersResponseDto(
                headers =
                    response.headers.map { (name, value) ->
                        HttpHeadersResponseDto.HttpHeaderDto(
                            name = name,
                            value = value,
                        )
                    },
            )
        }
}
