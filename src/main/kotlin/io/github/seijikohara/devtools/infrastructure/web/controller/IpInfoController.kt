package io.github.seijikohara.devtools.infrastructure.web.controller

import io.github.seijikohara.devtools.application.usecase.GetIpInfoUseCase
import io.github.seijikohara.devtools.infrastructure.web.dto.IpInfoResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for IP information endpoint.
 */
@RestController
@RequestMapping("\${application.api-base-path}")
@Tag(name = "Network Information")
class IpInfoController(
    private val getIpInfoUseCase: GetIpInfoUseCase,
) {
    /**
     * Retrieves client IP information.
     *
     * @param request Server HTTP request
     * @return [IpInfoResponseDto] instance
     */
    @GetMapping("/ip")
    @Operation(summary = "Get client IP information")
    suspend fun getIpInfo(request: ServerHttpRequest): IpInfoResponseDto =
        getIpInfoUseCase(
            GetIpInfoUseCase.Request(
                xForwardedFor = request.headers.getFirst("X-Forwarded-For"),
                xRealIp = request.headers.getFirst("X-Real-IP"),
                remoteAddress =
                    request
                        .remoteAddress
                        ?.address
                        ?.hostAddress,
                canonicalHostName =
                    request
                        .remoteAddress
                        ?.address
                        ?.canonicalHostName,
            ),
        ).let { response ->
            IpInfoResponseDto(
                ipAddress = response.ipAddress,
                hostName = response.hostName,
            )
        }
}
