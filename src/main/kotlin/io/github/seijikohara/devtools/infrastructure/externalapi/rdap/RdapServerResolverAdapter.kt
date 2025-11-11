package io.github.seijikohara.devtools.infrastructure.externalapi.rdap

import com.google.common.collect.RangeMap
import inet.ipaddr.IPAddress
import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.infrastructure.config.ApplicationProperties
import org.slf4j.Logger
import java.net.URI

/**
 * Adapter implementation for resolving RDAP server URIs.
 *
 * Uses Guava's RangeMap for efficient IP address range lookups.
 * Loads RDAP definitions at initialization time.
 *
 * @property rangeMap Mapping from IP address ranges to RDAP server URIs
 */
class RdapServerResolverAdapter(
    logger: Logger,
    applicationProperties: ApplicationProperties,
) : RdapServerResolver {
    private val rangeMap: RangeMap<IPAddress, URI> =
        RdapRangeMapBuilder.build(logger, applicationProperties)

    /**
     * Resolves the RDAP server URI for the given IP address.
     *
     * Performs a range lookup in the pre-built RangeMap to find the appropriate
     * RDAP server for the IP address.
     *
     * @param ipAddress The IP address to resolve
     * @return Result containing the RDAP server URI on success, or RdapServerNotFoundException on failure
     */
    override fun invoke(ipAddress: IpAddress): Result<URI> =
        runCatching {
            rangeMap[ipAddress.toInetIPAddress()]
                ?: throw RdapServerNotFoundException("No RDAP server found for ${ipAddress.value}")
        }
}
