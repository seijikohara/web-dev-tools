package io.github.seijikohara.devtools.application.usecase

/**
 * Use case for retrieving client IP address and hostname information.
 *
 * @see Request
 * @see Response
 */
fun interface GetIpInfoUseCase {
    /**
     * Retrieves client IP address and hostname information.
     *
     * @param request The request containing HTTP headers and connection information
     * @return [Response] containing the client's IP address and hostname
     */
    operator fun invoke(request: Request): Response

    /**
     * Request for retrieving IP information.
     *
     * @property xForwardedFor X-Forwarded-For header value
     * @property xRealIp X-Real-IP header value
     * @property remoteAddress Remote socket address
     * @property canonicalHostName Canonical hostname
     */
    data class Request(
        val xForwardedFor: String?,
        val xRealIp: String?,
        val remoteAddress: String?,
        val canonicalHostName: String?,
    )

    /**
     * Response containing IP information.
     *
     * @property ipAddress The client's IP address
     * @property hostName The client's hostname
     */
    data class Response(
        val ipAddress: String?,
        val hostName: String?,
    )
}

/**
 * Creates a [GetIpInfoUseCase] instance.
 *
 * @return A [GetIpInfoUseCase] instance
 */
fun getIpInfoUseCase(): GetIpInfoUseCase =
    GetIpInfoUseCase { request ->
        GetIpInfoUseCase.Response(
            ipAddress =
                sequenceOf(
                    request.xForwardedFor
                        ?.split(",")
                        ?.firstOrNull()
                        ?.trim(),
                    request.xRealIp?.trim(),
                    request.remoteAddress,
                ).filterNotNull()
                    .firstOrNull(),
            hostName = request.canonicalHostName,
        )
    }
