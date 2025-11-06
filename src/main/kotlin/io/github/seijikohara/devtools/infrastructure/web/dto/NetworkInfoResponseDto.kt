package io.github.seijikohara.devtools.infrastructure.web.dto

import io.github.seijikohara.devtools.application.usecase.GetGeoLocationUseCase
import io.github.seijikohara.devtools.application.usecase.GetRdapInformationUseCase
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Response DTO for RDAP information endpoint.
 *
 * Maintains compatibility with existing API contract.
 *
 * @property rdap RDAP information as raw JSON data
 */
@Serializable
data class RdapResponseDto(
    val rdap: Map<String, @Contextual JsonElement>?,
)

/**
 * Response DTO for GeoIP information endpoint.
 *
 * Maintains compatibility with existing API contract.
 *
 * @property geo Geographic location information as raw JSON data
 */
@Serializable
data class GeoResponseDto(
    val geo: Map<String, @Contextual JsonElement>?,
)

/**
 * Converts use case response to DTO.
 *
 * @receiver Response from the get RDAP information use case
 * @return RDAP response DTO
 */
fun GetRdapInformationUseCase.Response.toDto(): RdapResponseDto = RdapResponseDto(rdap = rdapInformation.rawData)

/**
 * Converts use case response to DTO.
 *
 * @receiver Response from the get geo location use case
 * @return Geo response DTO
 */
fun GetGeoLocationUseCase.Response.toDto(): GeoResponseDto = GeoResponseDto(geo = geoLocation.rawData)
