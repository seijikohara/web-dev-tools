package io.github.seijikohara.devtools.infrastructure.web.dto

/**
 * Data transfer object for IP information response.
 *
 * Represents the client's IP address and hostname information.
 *
 * @property ipAddress Client's IP address
 * @property hostName Client's hostname
 */
data class IpInfoResponseDto(
    val ipAddress: String?,
    val hostName: String?,
)
