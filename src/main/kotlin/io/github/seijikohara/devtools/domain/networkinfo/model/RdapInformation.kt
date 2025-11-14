package io.github.seijikohara.devtools.domain.networkinfo.model

import java.time.Duration
import java.time.Instant

/**
 * Represents RDAP (Registry Data Access Protocol) information for an IP address.
 *
 * Encapsulates registration information obtained from an RDAP query.
 * Fields are nullable as RDAP data may be incomplete or unavailable depending on
 * the registry's data disclosure policies.
 *
 * @property ipAddress The IP address for which this RDAP data was obtained
 * @property handle The unique identifier assigned by the registry, null if not provided
 * @property name The name of the registrant or organization, null if not provided
 * @property country The ISO 3166-1 alpha-2 country code of the registrant, null if not provided
 * @property registeredAt The timestamp when the IP address block was registered, null if not available
 * @see IpAddress
 * @see CountryCode
 */
data class RdapInformation(
    val ipAddress: IpAddress,
    val handle: String?,
    val name: String?,
    val country: CountryCode?,
    val registeredAt: Instant?,
) {
    /**
     * Checks if this RDAP information is considered stale based on registration age.
     *
     * Returns `false` if [registeredAt] is `null`.
     *
     * @param threshold The duration after which information is considered stale
     * @return `true` if the information is older than the threshold, `false` otherwise
     */
    fun isStale(threshold: Duration = Duration.ofDays(365)): Boolean =
        registeredAt?.let {
            Duration.between(it, Instant.now()) > threshold
        } ?: false
}
