package io.github.seijikohara.devtools.infrastructure.web.controller

import io.github.seijikohara.devtools.application.usecase.GetGeoLocationUseCase
import io.github.seijikohara.devtools.application.usecase.GetRdapInformationUseCase
import io.github.seijikohara.devtools.domain.networkinfo.repository.RdapServerNotFoundException
import io.github.seijikohara.devtools.infrastructure.web.dto.GeoResponseDto
import io.github.seijikohara.devtools.infrastructure.web.dto.RdapResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.server.ResponseStatusException

/**
 * REST controller for network information endpoints.
 *
 * Returns complete responses from external APIs:
 * - RDAP: RFC 9083 compliant response
 * - GeoIP: ipapi.co full response
 */
@RestController
@RequestMapping("\${application.api-base-path}")
@Tag(name = "Network Information")
class NetworkInfoController(
    private val getRdapInformationUseCase: GetRdapInformationUseCase,
    private val getGeoLocationUseCase: GetGeoLocationUseCase,
) {
    /**
     * Retrieves RDAP information for an IP address.
     *
     * Returns the complete RFC 9083 RDAP response including all available fields
     * such as handle, name, country, status, entities, events, links, etc.
     *
     * @param ip IP address
     * @return [RdapResponseDto] Complete RDAP response
     * @see <a href="https://datatracker.ietf.org/doc/rfc9083/">RFC 9083</a>
     */
    @GetMapping("/rdap/{ip}")
    @Operation(summary = "Get RDAP information for an IP address (RFC 9083 compliant)")
    suspend fun getRdapInformation(
        @Parameter(description = "IP address to lookup", example = "8.8.8.8")
        @PathVariable
        ip: String,
    ): RdapResponseDto =
        getRdapInformationUseCase(
            GetRdapInformationUseCase.Request(ipAddressString = ip),
        ).fold(
            onSuccess = { it.rdapInformation },
            onFailure = { error ->
                when (error) {
                    is RdapServerNotFoundException ->
                        throw ResponseStatusException(HttpStatus.NOT_FOUND, "RDAP server not found for IP: $ip")
                    is WebClientResponseException.NotFound ->
                        throw ResponseStatusException(HttpStatus.NOT_FOUND, "RDAP information not found for IP: $ip")
                    is IllegalArgumentException ->
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, error.message)
                    else -> throw error
                }
            },
        )

    /**
     * Retrieves geographic location for an IP address.
     *
     * Returns the complete ipapi.co response including all available fields
     * such as city, region, country, coordinates, timezone, ASN, organization, etc.
     *
     * @param ip IP address
     * @return [GeoResponseDto] Complete ipapi.co response
     * @see <a href="https://ipapi.co/api/">ipapi.co API Reference</a>
     */
    @GetMapping("/geo/{ip}")
    @Operation(summary = "Get geographic location for an IP address (ipapi.co full response)")
    suspend fun getGeoLocation(
        @Parameter(description = "IP address to lookup", example = "8.8.8.8")
        @PathVariable
        ip: String,
    ): GeoResponseDto =
        getGeoLocationUseCase(
            GetGeoLocationUseCase.Request(ipAddressString = ip),
        ).fold(
            onSuccess = { it.geoLocation },
            onFailure = { error ->
                when (error) {
                    is IllegalArgumentException ->
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, error.message)
                    else -> throw error
                }
            },
        )
}
