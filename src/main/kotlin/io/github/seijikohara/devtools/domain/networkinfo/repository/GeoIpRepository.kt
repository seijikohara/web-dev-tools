package io.github.seijikohara.devtools.domain.networkinfo.repository

import io.github.seijikohara.devtools.domain.networkinfo.model.GeoLocation
import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress

/**
 * Repository interface for accessing geographic location information.
 *
 * @see GeoLocation
 * @see IpAddress
 */
fun interface GeoIpRepository {
    /**
     * Retrieves geographic location information for an IP address.
     *
     * @param ipAddress The IP address to query
     * @return [Result] containing [GeoLocation] on success, or failure with exception
     */
    suspend operator fun invoke(ipAddress: IpAddress): Result<GeoLocation>
}
