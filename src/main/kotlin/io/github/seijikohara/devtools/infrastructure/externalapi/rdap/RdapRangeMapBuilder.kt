package io.github.seijikohara.devtools.infrastructure.externalapi.rdap

import com.google.common.collect.ImmutableRangeMap
import com.google.common.collect.Range
import com.google.common.collect.RangeMap
import inet.ipaddr.IPAddress
import inet.ipaddr.IPAddressString
import io.github.seijikohara.devtools.infrastructure.config.ApplicationProperties
import io.github.seijikohara.devtools.infrastructure.externalapi.common.decodeJson
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory
import java.net.URI

private val logger = LoggerFactory.getLogger(RdapRangeMapBuilder::class.java)

/**
 * Builder for constructing IP address range to RDAP server URI mapping.
 */
object RdapRangeMapBuilder {
    /**
     * Builds range map for RDAP server URI resolution.
     *
     * @param applicationProperties Application configuration properties
     * @return [RangeMap] for IP address to RDAP server URI mapping
     */
    fun build(applicationProperties: ApplicationProperties): RangeMap<IPAddress, URI> =
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
                                        if (ipAddress != null) {
                                            put(Range.closed(ipAddress.lower, ipAddress.upper), rdapURI)
                                        } else {
                                            logger.warn("[RDAP] Invalid CIDR format in $type definition: $cidr")
                                        }
                                    }
                                }
                            }.build()
                    } ?: ImmutableRangeMap.of<IPAddress, URI>()
                }.let(builder::putAll)
            builder
        }.build()
}

/**
 * RDAP definition file structure as defined by IANA.
 */
@Serializable
internal data class RdapFileStructure(
    val description: String,
    val publication: String,
    val services: List<List<List<String>>>,
    val version: String,
)
