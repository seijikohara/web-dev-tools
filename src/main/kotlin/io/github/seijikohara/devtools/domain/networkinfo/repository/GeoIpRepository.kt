package io.github.seijikohara.devtools.domain.networkinfo.repository

import io.github.seijikohara.devtools.domain.networkinfo.model.GeoLocation
import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress

/**
 * Repository interface (port) for accessing GeoIP information.
 *
 * This is a functional interface that defines the contract for retrieving
 * geographic location information for an IP address.
 */
fun interface GeoIpRepository {
    /**
     * Retrieves geographic location information for the given IP address.
     *
     * @param ipAddress The IP address to look up
     * @return Result containing GeoLocation if successful, or a failure with an exception
     */
    suspend operator fun invoke(ipAddress: IpAddress): Result<GeoLocation>
}
