package io.github.seijikohara.devtools.domain.networkinfo.model

import kotlinx.serialization.json.JsonElement
import java.time.Duration
import java.time.Instant

/**
 * RDAP (Registry Data Access Protocol) information entity.
 *
 * Represents registration data for an IP address retrieved from RDAP servers.
 *
 * @property ipAddress The IP address for which RDAP data is provided
 * @property handle The unique identifier assigned by the registry, or null if unavailable
 * @property name The name of the registrant or organization, or null if unavailable
 * @property country The ISO 3166-1 alpha-2 country code of the registrant, or null if unavailable
 * @property registeredAt The timestamp when the IP address was registered, or null if unavailable
 * @property rawData The raw JSON data received from the RDAP service
 */
data class RdapInformation(
    val ipAddress: IpAddress,
    val handle: String?,
    val name: String?,
    val country: CountryCode?,
    val registeredAt: Instant?,
    val rawData: Map<String, JsonElement>,
) {
    /**
     * Checks if this RDAP information is considered stale.
     *
     * @param threshold The duration after which information is considered stale (default: 365 days)
     * @return true if the information is older than the threshold
     */
    fun isStale(threshold: Duration = Duration.ofDays(365)): Boolean =
        registeredAt?.let {
            Duration.between(it, Instant.now()) > threshold
        } ?: false
}
