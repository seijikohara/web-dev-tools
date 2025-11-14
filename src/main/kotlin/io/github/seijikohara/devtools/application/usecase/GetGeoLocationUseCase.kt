package io.github.seijikohara.devtools.application.usecase

import io.github.seijikohara.devtools.domain.common.extensions.flatMap
import io.github.seijikohara.devtools.domain.networkinfo.model.GeoLocation
import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.repository.GeoIpRepository

/**
 * Use case for retrieving geographic location information for an IP address.
 *
 * @see Request
 * @see Response
 * @see GeoLocation
 */
fun interface GetGeoLocationUseCase {
    /**
     * Retrieves geographic location information for an IP address.
     *
     * @param request The request containing the IP address string
     * @return [Result] containing [Response] on success, or failure with exception
     */
    suspend operator fun invoke(request: Request): Result<Response>

    /**
     * Request for retrieving geographic location information.
     *
     * @property ipAddressString IP address in string format
     */
    data class Request(
        val ipAddressString: String,
    )

    /**
     * Response containing geographic location information.
     *
     * @property geoLocation Geographic location data
     */
    data class Response(
        val geoLocation: GeoLocation,
    )
}

/**
 * Creates a [GetGeoLocationUseCase] instance.
 *
 * @param geoIpRepository The GeoIP repository implementation
 * @return A [GetGeoLocationUseCase] instance
 */
fun getGeoLocationUseCase(geoIpRepository: GeoIpRepository): GetGeoLocationUseCase =
    GetGeoLocationUseCase { request ->
        IpAddress
            .of(request.ipAddressString)
            .flatMap { ipAddress ->
                geoIpRepository(ipAddress)
            }.map { geoLocation ->
                GetGeoLocationUseCase.Response(geoLocation)
            }
    }
