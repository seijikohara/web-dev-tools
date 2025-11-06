package io.github.seijikohara.devtools.domain.networkinfo.model

import kotlinx.serialization.json.JsonElement

/**
 * Geographic location information entity.
 *
 * Represents geographic data associated with an IP address,
 * including country, city, and geographical coordinates.
 *
 * @property ipAddress The IP address for which location data is provided
 * @property countryCode The ISO 3166-1 alpha-2 country code, or null if unavailable
 * @property city The city name, or null if unavailable
 * @property latitude The latitude coordinate in decimal degrees, or null if unavailable
 * @property longitude The longitude coordinate in decimal degrees, or null if unavailable
 * @property rawData The raw JSON data received from the GeoIP service
 */
data class GeoLocation(
    val ipAddress: IpAddress,
    val countryCode: CountryCode?,
    val city: String?,
    val latitude: Double?,
    val longitude: Double?,
    val rawData: Map<String, JsonElement>,
)
