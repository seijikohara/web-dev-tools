package io.github.seijikohara.devtools.infrastructure.externalapi.rdap

import com.google.common.collect.ImmutableRangeMap
import com.google.common.collect.Range
import com.google.common.collect.RangeMap
import inet.ipaddr.IPAddress
import inet.ipaddr.IPAddressString
import io.github.seijikohara.devtools.infrastructure.config.ApplicationProperties
import io.github.seijikohara.devtools.infrastructure.externalapi.common.decodeJson
import kotlinx.serialization.Serializable
import org.slf4j.Logger
import java.net.URI

/**
 * Builder for constructing IP address range to RDAP server URI mapping.
 *
 * Loads IPv4 and IPv6 RDAP definitions from configuration files
 * and constructs an efficient Guava RangeMap for lookups.
 */
object RdapRangeMapBuilder {
    /**
     * Builds RangeMap for RDAP server URI resolution.
     *
     * @param logger Logger for initialization messages
     * @param applicationProperties Application configuration containing RDAP definitions
     * @return RangeMap mapping IP address ranges to RDAP server URIs
     */
    fun build(
        logger: Logger,
        applicationProperties: ApplicationProperties,
    ): RangeMap<IPAddress, URI> =
        listOf(
            applicationProperties.network.rdap.ipv4 to "IPV4",
            applicationProperties.network.rdap.ipv6 to "IPV6",
        ).onEach { (resource, type) ->
            logger.info("[RDAP] Load $type definition : $resource")
        }.fold(ImmutableRangeMap.builder<IPAddress, URI>()) { builder, (resource, type) ->
            resource.inputStream
                .reader()
                .use { it.readText() }
                .let { json ->
                    decodeJson<RdapFileStructure>(json)?.let { rdapFileStructure ->
                        ImmutableRangeMap
                            .builder<IPAddress, URI>()
                            .apply {
                                rdapFileStructure.services.forEach { service ->
                                    val rdapURI = URI.create(service[1][0])
                                    service[0].forEach { cidr ->
                                        val ipAddress = IPAddressString(cidr).address
                                        put(Range.closed(ipAddress.lower, ipAddress.upper), rdapURI)
                                    }
                                }
                            }.build()
                    } ?: ImmutableRangeMap.of<IPAddress, URI>()
                }.let(builder::putAll)
            builder
        }.build()
}

/**
 * RDAP definition file structure.
 *
 * Represents the JSON structure of RDAP server mapping files
 * as defined by IANA (Internet Assigned Numbers Authority).
 */
@Serializable
internal data class RdapFileStructure(
    val description: String,
    val publication: String,
    val services: List<List<List<String>>>,
    val version: String,
)
