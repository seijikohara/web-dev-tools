package io.github.seijikohara.devtools.infrastructure.externalapi.geoip

import io.github.seijikohara.devtools.config.ApplicationProperties
import io.github.seijikohara.devtools.domain.networkinfo.model.GeoLocation
import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.repository.GeoIpRepository
import io.github.seijikohara.devtools.infrastructure.externalapi.common.decodeJsonToElements
import io.github.seijikohara.devtools.infrastructure.externalapi.geoip.toGeoLocation
import org.slf4j.Logger
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

/**
 * Adapter implementation for GeoIP repository.
 *
 * Retrieves geographic location information from external GeoIP services via HTTP.
 * Uses functional programming approach with expression chains.
 *
 * @property logger Logger for recording API requests
 * @property webClient HTTP client for external API communication
 * @property applicationProperties Application configuration containing GeoIP service URI
 */
class GeoIpRepositoryAdapter(
    private val logger: Logger,
    private val webClient: WebClient,
    private val applicationProperties: ApplicationProperties,
) : GeoIpRepository {
    /**
     * Retrieves geographic location information for the given IP address.
     *
     * Constructs a request to the configured GeoIP service endpoint and transforms
     * the JSON response into a domain model object.
     *
     * @param ipAddress The IP address to look up
     * @return Result containing GeoLocation on success, or error on failure
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
            }.mapCatching(::decodeJsonToElements)
            .mapCatching { rawData -> rawData.toGeoLocation(ipAddress) }
}
