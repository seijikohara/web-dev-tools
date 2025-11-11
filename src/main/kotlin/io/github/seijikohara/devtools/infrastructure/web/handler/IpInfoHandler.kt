package io.github.seijikohara.devtools.infrastructure.web.handler

import io.github.seijikohara.devtools.application.usecase.GetIpInfoUseCase
import io.github.seijikohara.devtools.infrastructure.web.dto.IpInfoResponseDto
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

/**
 * Handler function for IP information endpoint.
 *
 * Returns the client's IP address and hostname through the GetIpInfoUseCase.
 *
 * @param request The server request containing client connection information
 * @param useCase The use case for extracting IP information
 * @return ServerResponse with client IP address and hostname
 */
suspend fun handleGetIpInfo(
    request: ServerRequest,
    useCase: GetIpInfoUseCase,
): ServerResponse =
    ServerResponse
        .ok()
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValueAndAwait(
            useCase(
                GetIpInfoUseCase.Request(
                    xForwardedFor = request.headers().firstHeader("X-Forwarded-For"),
                    xRealIp = request.headers().firstHeader("X-Real-IP"),
                    remoteAddress =
                        request
                            .remoteAddress()
                            .orElse(null)
                            ?.address
                            ?.hostAddress,
                    canonicalHostName =
                        request
                            .remoteAddress()
                            .orElse(null)
                            ?.address
                            ?.canonicalHostName,
                ),
            ).let { response ->
                IpInfoResponseDto(
                    ipAddress = response.ipAddress,
                    hostName = response.hostName,
                )
            },
        )
