@file:Suppress("ktlint:standard:no-consecutive-comments")

package io.github.seijikohara.devtools.infrastructure.externalapi.geoip

import io.github.seijikohara.devtools.domain.networkinfo.model.CountryCode
import io.github.seijikohara.devtools.domain.networkinfo.model.GeoLocation
import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.infrastructure.externalapi.common.decodeJsonToElements
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

/**
 * GeoIP API response data.
 *
 * @property data JSON response data
 */
@ConsistentCopyVisibility
data class GeoIpResponse internal constructor(
    val data: Map<String, JsonElement>,
)

/**
 * Decodes JSON string to [GeoIpResponse].
 *
 * @param json JSON string to decode
 * @return [GeoIpResponse] instance
 */
fun decodeJsonToGeoIpResponse(json: String?): GeoIpResponse = GeoIpResponse(decodeJsonToElements(json))

/**
 * Converts to [GeoLocation].
 *
 * @receiver Response data
 * @param ipAddress IP address
 * @return [GeoLocation] instance
 */
fun GeoIpResponse.toDomain(ipAddress: IpAddress): GeoLocation =
    GeoLocation(
        ipAddress = ipAddress,
        countryCode =
            (
                data["country_code"]?.jsonPrimitive?.content
                    ?: data["country"]?.jsonPrimitive?.content
            )?.let(CountryCode::of)
                ?.getOrNull(),
        city = data["city"]?.jsonPrimitive?.content,
        latitude = data["latitude"]?.jsonPrimitive?.content?.toDoubleOrNull(),
        longitude = data["longitude"]?.jsonPrimitive?.content?.toDoubleOrNull(),
    )
