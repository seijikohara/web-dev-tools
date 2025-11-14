package io.github.seijikohara.devtools.domain.networkinfo.repository

import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapInformation

/**
 * Repository interface for accessing RDAP information.
 *
 * Provides access to RDAP (Registry Data Access Protocol) registration data.
 *
 * @see RdapInformation
 * @see IpAddress
 */
fun interface RdapRepository {
    /**
     * Retrieves RDAP information for an IP address.
     *
     * @param ipAddress The IP address to query
     * @return [Result] containing [RdapInformation] on success, or failure with exception
     */
    suspend operator fun invoke(ipAddress: IpAddress): Result<RdapInformation>
}
