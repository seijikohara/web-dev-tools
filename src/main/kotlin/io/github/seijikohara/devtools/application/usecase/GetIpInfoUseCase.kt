package io.github.seijikohara.devtools.application.usecase

/**
 * Use case for retrieving client IP address and hostname information.
 *
 * This is a functional interface that encapsulates the business logic
 * for extracting client IP information from HTTP request headers and connection data.
 */
fun interface GetIpInfoUseCase {
    /**
     * Executes the use case.
     *
     * @param request The use case request containing HTTP headers and connection information
     * @return Response containing the client's IP address and hostname
     */
    operator fun invoke(request: Request): Response

    /**
     * Request for retrieving IP information.
     *
     * @property xForwardedFor X-Forwarded-For header value (proxy forwarding chain)
     * @property xRealIp X-Real-IP header value (direct proxy IP)
     * @property remoteAddress Remote socket address from connection
     * @property canonicalHostName Canonical hostname from reverse DNS lookup
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
     * @property ipAddress The client's IP address (extracted from headers or remote address)
     * @property hostName The client's hostname (from reverse DNS lookup)
     */
    data class Response(
        val ipAddress: String?,
        val hostName: String?,
    )
}

/**
 * Factory function to create a GetIpInfoUseCase instance.
 *
 * IP address extraction priority:
 * 1. X-Forwarded-For header (first IP in the chain, for proxied requests)
 * 2. X-Real-IP header (for direct proxy connections)
 * 3. Remote address (for direct connections)
 *
 * @return A GetIpInfoUseCase instance
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
