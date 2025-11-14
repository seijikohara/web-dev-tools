package io.github.seijikohara.devtools.infrastructure.externalapi.geoip

import io.github.seijikohara.devtools.domain.networkinfo.model.GeoLocation
import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.repository.GeoIpRepository
import io.github.seijikohara.devtools.infrastructure.config.ApplicationProperties
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

/**
 * Adapter implementation for GeoIP repository.
 *
 * @property webClient HTTP client for external API calls
 * @property applicationProperties Application configuration properties
 */
class GeoIpRepositoryAdapter(
    private val webClient: WebClient,
    private val applicationProperties: ApplicationProperties,
) : GeoIpRepository {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Retrieves geographic location information for an IP address.
     *
     * @param ipAddress The IP address to query
     * @return [Result] containing [GeoLocation] on success, or failure with exception
     */
    override suspend fun invoke(ipAddress: IpAddress): Result<GeoLocation> =
        runCatching { "${applicationProperties.network.geo.uri}/${ipAddress.value}/json" }
            .onSuccess { uri -> logger.info("[GEO] Fetching: $uri") }
            .mapCatching { uri ->
                webClient
                    .get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .awaitBody<String>()
            }.mapCatching(::decodeJsonToGeoIpResponse)
            .mapCatching { it.toDomain(ipAddress) }
}
