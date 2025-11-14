package io.github.seijikohara.devtools.infrastructure.externalapi.rdap

import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapInformation
import io.github.seijikohara.devtools.domain.networkinfo.model.buildQueryUri
import io.github.seijikohara.devtools.domain.networkinfo.repository.RdapRepository
import io.github.seijikohara.devtools.domain.networkinfo.repository.RdapServerResolver
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

/**
 * Adapter implementation for RDAP repository.
 *
 * @property webClient HTTP client for external API calls
 * @property rdapServerResolver RDAP server resolver
 */
class RdapRepositoryAdapter(
    private val webClient: WebClient,
    private val rdapServerResolver: RdapServerResolver,
) : RdapRepository {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Retrieves RDAP information for an IP address.
     *
     * @param ipAddress The IP address to query
     * @return [Result] containing [RdapInformation] on success, or failure with exception
     */
    override suspend fun invoke(ipAddress: IpAddress): Result<RdapInformation> =
        rdapServerResolver(ipAddress)
            .mapCatching { server -> server.buildQueryUri(ipAddress) }
            .onSuccess { uri -> logger.info("[RDAP] Fetching: $uri") }
            .mapCatching { uri ->
                webClient
                    .get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .awaitBody<String>()
            }.mapCatching(::decodeJsonToRdapResponse)
            .mapCatching { it.toDomain(ipAddress) }
}
