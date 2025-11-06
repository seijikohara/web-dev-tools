package io.github.seijikohara.devtools.infrastructure.externalapi.rdap

import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import java.net.URI

/**
 * Resolver for finding the appropriate RDAP server URI for a given IP address.
 *
 * This is a functional interface that encapsulates the logic for determining
 * which RDAP server to query based on IP address ranges.
 */
fun interface RdapServerResolver {
    /**
     * Resolves the RDAP server URI for the given IP address.
     *
     * @param ipAddress The IP address to resolve
     * @return Result containing the RDAP server URI if found, or a failure with an exception
     */
    operator fun invoke(ipAddress: IpAddress): Result<URI>
}

/**
 * Exception thrown when no RDAP server URI can be found for an IP address.
 */
class RdapServerNotFoundException(
    message: String,
) : RuntimeException(message)
