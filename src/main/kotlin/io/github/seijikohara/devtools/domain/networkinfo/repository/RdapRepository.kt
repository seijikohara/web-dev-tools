package io.github.seijikohara.devtools.domain.networkinfo.repository

import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapInformation

/**
 * Repository interface (port) for accessing RDAP information.
 *
 * This is a functional interface that defines the contract for retrieving
 * RDAP (Registry Data Access Protocol) information for an IP address.
 */
fun interface RdapRepository {
    /**
     * Retrieves RDAP information for the given IP address.
     *
     * @param ipAddress The IP address to look up
     * @return Result containing RdapInformation if successful, or a failure with an exception
     */
    suspend operator fun invoke(ipAddress: IpAddress): Result<RdapInformation>
}
