package net.relaxism.devtools.webdevtools.component.api

import com.google.common.collect.ImmutableRangeMap
import com.google.common.collect.Range
import com.google.common.collect.RangeMap
import inet.ipaddr.IPAddress
import inet.ipaddr.IPAddressString
import kotlinx.coroutines.coroutineScope
import net.relaxism.devtools.webdevtools.config.ApplicationProperties
import net.relaxism.devtools.webdevtools.utils.JsonUtils
import net.relaxism.devtools.webdevtools.utils.PathUtils
import org.slf4j.Logger
import org.springframework.beans.factory.InitializingBean
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.io.BufferedReader
import java.io.InputStream
import java.net.URI

@Component
class RdapClient(
    private val logger: Logger,
    private val applicationProperties: ApplicationProperties,
    private val webClient: WebClient,
) : InitializingBean {
    private lateinit var rdapUriByIpAddressRange: RangeMap<IPAddress, URI>

    override fun afterPropertiesSet() {
        val ipv4RdapJsonResource = applicationProperties.network.rdap.ipv4
        logger.info("[RDAP] Load IPV4 definition : $ipv4RdapJsonResource")
        val rdapUriByIpV4AddressRange = resolveJson(inputStreamToString(ipv4RdapJsonResource.inputStream))

        val ipv6RdapJsonResource = applicationProperties.network.rdap.ipv6
        logger.info("[RDAP] Load IPV6 definition : $ipv6RdapJsonResource")
        val rdapUriByIpV6AddressRange = resolveJson(inputStreamToString(ipv6RdapJsonResource.inputStream))

        rdapUriByIpAddressRange =
            ImmutableRangeMap
                .builder<IPAddress, URI>()
                .putAll(rdapUriByIpV4AddressRange)
                .putAll(rdapUriByIpV6AddressRange)
                .build()
    }

    private fun inputStreamToString(inputStream: InputStream): String {
        val reader = BufferedReader(inputStream.reader())
        reader.use { return it.readText() }
    }

    private fun resolveJson(json: String): RangeMap<IPAddress, URI> {
        val rdapFileStructure = JsonUtils.fromJson(json, RdapFileStructure::class.java)

        val ipRangeMapBuilder = ImmutableRangeMap.builder<IPAddress, URI>()
        rdapFileStructure?.services?.forEach { service ->
            val cidrList = service[0]
            val rdapURI = URI.create(service[1][0])
            cidrList.forEach { cidr ->
                val ipAddress = IPAddressString(cidr).address
                val lowerIPAddress = ipAddress.lower
                val upperIPAddress = ipAddress.upper
                ipRangeMapBuilder.put(Range.closed(lowerIPAddress, upperIPAddress), rdapURI)
            }
        }
        return ipRangeMapBuilder.build()
    }

    suspend fun getRdapByIpAddress(ipAddressString: String): Map<String, Any?> =
        coroutineScope {
            val ipAddress = IPAddressString(ipAddressString).address
            val rdapUri = rdapUriByIpAddressRange[ipAddress]

            if (rdapUri == null) {
                logger.warn("[RDAP] Not found RDAP URI for $ipAddressString")
                throw NotFoundRdapUriException("Not found RDAP URI for $ipAddressString")
            }

            val uri = PathUtils.concatenate(rdapUriByIpAddressRange[ipAddress].toString(), "ip", ipAddressString)
            logger.info("[RDAP] $uri")

            val jsonString =
                webClient
                    .get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .awaitBody<String>()
            JsonUtils.fromJson(jsonString)
        }

    data class RdapFileStructure(
        val description: String,
        val publication: String,
        val services: List<List<List<String>>>,
        val version: String,
    )

    class NotFoundRdapUriException(
        message: String,
    ) : RuntimeException(message)
}
