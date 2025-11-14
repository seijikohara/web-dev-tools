package io.github.seijikohara.devtools.domain.networkinfo.model

/**
 * Represents geographic location information associated with an IP address.
 *
 * Encapsulates geolocation data obtained from GeoIP lookup services.
 * Fields are nullable as geolocation data may be incomplete or unavailable.
 *
 * @property ipAddress The IP address for which this location data was obtained
 * @property countryCode The ISO 3166-1 alpha-2 country code, null if not available
 * @property city The city name, null if not available
 * @property latitude The latitude coordinate in decimal degrees, null if not available
 * @property longitude The longitude coordinate in decimal degrees, null if not available
 * @see IpAddress
 * @see CountryCode
 */
data class GeoLocation(
    val ipAddress: IpAddress,
    val countryCode: CountryCode?,
    val city: String?,
    val latitude: Double?,
    val longitude: Double?,
)
