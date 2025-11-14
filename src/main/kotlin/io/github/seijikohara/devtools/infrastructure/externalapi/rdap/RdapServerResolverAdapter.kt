package io.github.seijikohara.devtools.infrastructure.externalapi.rdap

import com.google.common.collect.RangeMap
import inet.ipaddr.IPAddress
import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapServerUri
import io.github.seijikohara.devtools.domain.networkinfo.repository.RdapServerNotFoundException
import io.github.seijikohara.devtools.domain.networkinfo.repository.RdapServerResolver
import io.github.seijikohara.devtools.infrastructure.config.ApplicationProperties
import java.net.URI

/**
 * Adapter implementation for resolving RDAP server URIs.
 */
class RdapServerResolverAdapter(
    applicationProperties: ApplicationProperties,
) : RdapServerResolver {
    private val rangeMap: RangeMap<IPAddress, URI> =
        RdapRangeMapBuilder.build(applicationProperties)

    /**
     * Resolves the RDAP server URI for an IP address.
     *
     * @param ipAddress The IP address to query
     * @return [Result] containing [RdapServerUri] on success, or failure with exception
     */
    override fun invoke(ipAddress: IpAddress): Result<RdapServerUri> =
        runCatching {
            rangeMap[ipAddress.toInetIPAddress()]
                ?.let(RdapServerUri::of)
                ?: throw RdapServerNotFoundException("No RDAP server found for ${ipAddress.value}")
        }
}
