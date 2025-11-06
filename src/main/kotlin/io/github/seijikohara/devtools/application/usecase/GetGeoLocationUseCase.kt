package io.github.seijikohara.devtools.application.usecase

import io.github.seijikohara.devtools.domain.networkinfo.model.GeoLocation
import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.repository.GeoIpRepository

/**
 * Use case for retrieving geographic location information for an IP address.
 *
 * This is a functional interface that encapsulates the business logic
 * for fetching GeoIP data.
 */
fun interface GetGeoLocationUseCase {
    /**
     * Executes the use case.
     *
     * @param request The use case request containing the IP address string
     * @return Result containing Response if successful, or a failure with an exception
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
     * @property geoLocation Geographic location data for the requested IP address
     */
    data class Response(
        val geoLocation: GeoLocation,
    )
}

/**
 * Factory function to create a GetGeoLocationUseCase instance.
 *
 * @param geoIpRepository The GeoIP repository implementation
 * @return A GetGeoLocationUseCase instance
 */
fun getGeoLocationUseCase(geoIpRepository: GeoIpRepository): GetGeoLocationUseCase =
    GetGeoLocationUseCase { request ->
        IpAddress
            .of(request.ipAddressString)
            .mapCatching { ipAddress ->
                geoIpRepository(ipAddress).getOrThrow()
            }.map { geoLocation ->
                GetGeoLocationUseCase.Response(geoLocation)
            }
    }
