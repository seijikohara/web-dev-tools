package io.github.seijikohara.devtools.domain.networkinfo.repository

import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapInformation

/**
 * Repository interface for accessing RDAP information.
 *
 * Provides access to RDAP (Registry Data Access Protocol) registration data.
 * Returns the complete response as a domain model [RdapInformation].
 *
 * @see RdapInformation
 * @see IpAddress
 * @see <a href="https://datatracker.ietf.org/doc/rfc9083/">RFC 9083</a>
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
