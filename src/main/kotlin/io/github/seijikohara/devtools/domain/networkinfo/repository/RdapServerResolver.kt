package io.github.seijikohara.devtools.domain.networkinfo.repository

import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapServerUri

/**
 * Resolver for determining the appropriate RDAP server for an IP address.
 *
 * @see RdapServerUri
 * @see IpAddress
 */
fun interface RdapServerResolver {
    /**
     * Resolves the RDAP server URI for an IP address.
     *
     * @param ipAddress The IP address to query
     * @return [Result] containing [RdapServerUri] on success, or failure with exception
     */
    operator fun invoke(ipAddress: IpAddress): Result<RdapServerUri>
}

/**
 * Exception thrown when no RDAP server can be found for an IP address.
 */
class RdapServerNotFoundException(
    message: String,
) : RuntimeException(message)
