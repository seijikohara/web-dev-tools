package net.relaxism.devtools.webdevtools.component.api

import com.google.common.collect.ImmutableRangeMap
import com.google.common.collect.Range
import com.google.common.collect.RangeMap
import inet.ipaddr.IPAddress
import inet.ipaddr.IPAddressString
import net.relaxism.devtools.webdevtools.config.ApplicationProperties
import net.relaxism.devtools.webdevtools.utils.JsonUtils
import net.relaxism.devtools.webdevtools.utils.PathUtils
import org.slf4j.Logger
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.io.BufferedReader
import java.io.InputStream
import java.net.URI

@Suppress("UnstableApiUsage")
@Component
class RdapClient(
    @Autowired private val logger: Logger,
    @Autowired private val applicationProperties: ApplicationProperties,
    @Autowired private val webClient: WebClient
) : InitializingBean {

    private lateinit var ipRangeMap: RangeMap<IPAddress, URI>
    private lateinit var defaultRdapURI: URI

    override fun afterPropertiesSet() {
        val ipv4RdapJsonResource = applicationProperties.network.rdap.ipv4
        logger.info("[RDAP] Load IPV4 definition : $ipv4RdapJsonResource")
        val ipv4RangeMap = resolveJson(inputStreamToString(ipv4RdapJsonResource.inputStream))

        val ipv6RdapJsonResource = applicationProperties.network.rdap.ipv6
        logger.info("[RDAP] Load IPV6 definition : $ipv6RdapJsonResource")
        val ipv6RangeMap = resolveJson(inputStreamToString(ipv6RdapJsonResource.inputStream))

        val allIPAddressRangeMap = ImmutableRangeMap.builder<IPAddress, URI>()
            .putAll(ipv4RangeMap)
            .putAll(ipv6RangeMap)
            .build()
        ipRangeMap = allIPAddressRangeMap
        defaultRdapURI = allIPAddressRangeMap.asMapOfRanges().values.distinct().sorted()[0]
    }

    private fun inputStreamToString(inputStream: InputStream): String {
        val reader = BufferedReader(inputStream.reader())
        reader.use { return it.readText() }
    }

    private fun resolveJson(json: String): RangeMap<IPAddress, URI> {
        val jsonMap = JsonUtils.fromJson(json)
        val services = jsonMap["services"] as List<List<List<String>>>

        val ipRangeMapBuilder = ImmutableRangeMap.builder<IPAddress, URI>()
        services.forEach { service ->
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

    fun getRdapByIpAddress(ipAddressString: String): Mono<Map<String, Any?>> {
        val ipAddress = IPAddressString(ipAddressString).address
        val uri = PathUtils.concatenate(
            (ipRangeMap[ipAddress] ?: defaultRdapURI).toString(),
            "ip",
            ipAddressString
        )
        logger.info("[RDAP] $uri")
        return webClient.get()
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToMono { response ->
                response.bodyToMono(String::class.java)
                    .map { responseBody -> JsonUtils.fromJson(responseBody) }
                    .switchIfEmpty(Mono.defer { Mono.just(mapOf()) })
            }
    }

}
