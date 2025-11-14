package io.github.seijikohara.devtools.infrastructure.web.dto

import io.github.seijikohara.devtools.application.usecase.GetGeoLocationUseCase
import io.github.seijikohara.devtools.application.usecase.GetRdapInformationUseCase
import java.time.Instant

/**
 * RDAP information response.
 *
 * @property ipAddress IP address
 * @property handle Registry handle
 * @property name Registrant name
 * @property country Country code
 * @property registeredAt Registration timestamp
 */
data class RdapResponseDto(
    val ipAddress: String,
    val handle: String?,
    val name: String?,
    val country: String?,
    val registeredAt: Instant?,
)

/**
 * Geographic location response.
 *
 * @property ipAddress IP address
 * @property countryCode Country code
 * @property city City name
 * @property latitude Latitude coordinate
 * @property longitude Longitude coordinate
 */
data class GeoResponseDto(
    val ipAddress: String,
    val countryCode: String?,
    val city: String?,
    val latitude: Double?,
    val longitude: Double?,
)

/**
 * Converts to [RdapResponseDto].
 *
 * @receiver Response data
 * @return [RdapResponseDto] instance
 */
fun GetRdapInformationUseCase.Response.toDto(): RdapResponseDto =
    RdapResponseDto(
        ipAddress = rdapInformation.ipAddress.value,
        handle = rdapInformation.handle,
        name = rdapInformation.name,
        country = rdapInformation.country?.value,
        registeredAt = rdapInformation.registeredAt,
    )

/**
 * Converts to [GeoResponseDto].
 *
 * @receiver Response data
 * @return [GeoResponseDto] instance
 */
fun GetGeoLocationUseCase.Response.toDto(): GeoResponseDto =
    GeoResponseDto(
        ipAddress = geoLocation.ipAddress.value,
        countryCode = geoLocation.countryCode?.value,
        city = geoLocation.city,
        latitude = geoLocation.latitude,
        longitude = geoLocation.longitude,
    )
