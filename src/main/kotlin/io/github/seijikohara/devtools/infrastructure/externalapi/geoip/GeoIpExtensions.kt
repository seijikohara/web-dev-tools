package io.github.seijikohara.devtools.infrastructure.externalapi.geoip

import io.github.seijikohara.devtools.domain.networkinfo.model.CountryCode
import io.github.seijikohara.devtools.domain.networkinfo.model.GeoLocation
import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

/**
 * Converts GeoIP API response to domain model.
 *
 * Transforms JSON responses from GeoIP services into domain objects.
 *
 * @receiver The raw JSON response as a map
 * @param ipAddress The IP address being queried
 * @return GeoLocation domain object
 */
fun Map<String, JsonElement>.toGeoLocation(ipAddress: IpAddress): GeoLocation =
    GeoLocation(
        ipAddress = ipAddress,
        countryCode =
            (
                this["country_code"]?.jsonPrimitive?.content
                    ?: this["country"]?.jsonPrimitive?.content
            )?.let(CountryCode::of)
                ?.getOrNull(),
        city = this["city"]?.jsonPrimitive?.content,
        latitude = this["latitude"]?.jsonPrimitive?.content?.toDoubleOrNull(),
        longitude = this["longitude"]?.jsonPrimitive?.content?.toDoubleOrNull(),
        rawData = this,
    )
