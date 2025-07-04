package net.relaxism.devtools.webdevtools.repository.api

import com.google.common.collect.ImmutableRangeMap
import com.google.common.collect.Range
import com.google.common.collect.RangeMap
import inet.ipaddr.IPAddress
import inet.ipaddr.IPAddressString
import kotlinx.serialization.Serializable
import net.relaxism.devtools.webdevtools.config.ApplicationProperties
import net.relaxism.devtools.webdevtools.utils.JsonUtils
import net.relaxism.devtools.webdevtools.utils.PathUtils
import org.slf4j.Logger
import org.springframework.beans.factory.InitializingBean
import org.springframework.http.MediaType
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.io.InputStream
import java.net.URI

@Repository
class RdapApiRepository(
    private val logger: Logger,
    private val applicationProperties: ApplicationProperties,
    private val webClient: WebClient,
) : InitializingBean {
    private lateinit var rdapUriByIpAddressRange: RangeMap<IPAddress, URI>

    override fun afterPropertiesSet() {
        rdapUriByIpAddressRange =
            listOf(
                applicationProperties.network.rdap.ipv4 to "IPV4",
                applicationProperties.network.rdap.ipv6 to "IPV6",
            ).fold(ImmutableRangeMap.builder<IPAddress, URI>()) { builder, (resource, type) ->
                logger.info("[RDAP] Load $type definition : $resource")
                builder.putAll(resolveJson(resource.inputStream.readText()))
                builder
            }.build()
    }

    private fun InputStream.readText(): String = reader().use { it.readText() }

    private fun resolveJson(json: String): RangeMap<IPAddress, URI> =
        JsonUtils.fromJson<RdapFileStructure>(json)?.let { rdapFileStructure ->
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
        } ?: ImmutableRangeMap.of()

    suspend fun getRdapByIpAddress(ipAddressString: String): Map<String, Any?> {
        val ipAddress = IPAddressString(ipAddressString).address
        val rdapUri =
            rdapUriByIpAddressRange[ipAddress]
                ?: run {
                    logger.warn("[RDAP] Not found RDAP URI for $ipAddressString")
                    throw NotFoundRdapUriException("Not found RDAP URI for $ipAddressString")
                }

        val uri = PathUtils.concatenate(rdapUri.toString(), "ip", ipAddressString)
        logger.info("[RDAP] $uri")

        return webClient
            .get()
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody<String>()
            .let(JsonUtils::fromJson)
    }

    @Serializable
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
