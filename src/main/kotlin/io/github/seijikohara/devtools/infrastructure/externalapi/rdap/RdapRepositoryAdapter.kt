package io.github.seijikohara.devtools.infrastructure.externalapi.rdap

import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapInformation
import io.github.seijikohara.devtools.domain.networkinfo.repository.RdapRepository
import io.github.seijikohara.devtools.infrastructure.externalapi.common.buildRdapUri
import io.github.seijikohara.devtools.infrastructure.externalapi.common.decodeJsonToElements
import io.github.seijikohara.devtools.infrastructure.externalapi.rdap.toRdapInformation
import org.slf4j.Logger
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

/**
 * Adapter implementation for RDAP repository.
 *
 * Retrieves RDAP information from external RDAP servers via HTTP.
 * Uses functional programming approach with expression chains.
 *
 * @property logger Logger for recording API requests
 * @property webClient HTTP client for external API communication
 * @property rdapServerResolver Resolver for determining the appropriate RDAP server
 */
class RdapRepositoryAdapter(
    private val logger: Logger,
    private val webClient: WebClient,
    private val rdapServerResolver: RdapServerResolver,
) : RdapRepository {
    /**
     * Retrieves RDAP information for the given IP address.
     *
     * Resolves the appropriate RDAP server for the IP address, constructs
     * the request URI, fetches the data, and transforms the JSON response
     * into a domain model object.
     *
     * @param ipAddress The IP address to look up
     * @return Result containing RdapInformation on success, or error on failure
     */
    override suspend fun invoke(ipAddress: IpAddress): Result<RdapInformation> =
        rdapServerResolver(ipAddress)
            .mapCatching { server -> server.buildRdapUri(ipAddress) }
            .onSuccess { uri -> logger.info("[RDAP] Fetching: $uri") }
            .mapCatching { uri ->
                webClient
                    .get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .awaitBody<String>()
            }.mapCatching(::decodeJsonToElements)
            .mapCatching { rawData -> rawData.toRdapInformation(ipAddress) }
}
