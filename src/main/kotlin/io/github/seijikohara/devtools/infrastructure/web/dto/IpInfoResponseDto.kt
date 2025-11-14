package io.github.seijikohara.devtools.infrastructure.web.dto

/**
 * IP information response.
 *
 * @property ipAddress IP address
 * @property hostName Hostname
 */
data class IpInfoResponseDto(
    val ipAddress: String?,
    val hostName: String?,
)
