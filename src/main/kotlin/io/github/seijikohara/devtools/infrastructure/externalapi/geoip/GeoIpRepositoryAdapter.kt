package io.github.seijikohara.devtools.infrastructure.externalapi.geoip

import io.github.seijikohara.devtools.domain.networkinfo.model.GeoLocation
import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.repository.GeoIpRepository
import io.github.seijikohara.devtools.infrastructure.config.ApplicationProperties
import io.github.seijikohara.devtools.infrastructure.externalapi.common.decodeJson
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

/**
 * Adapter implementation for GeoIP repository.
 *
 * Fetches geolocation data from ipapi.co and converts to domain model.
 *
 * @property webClient HTTP client for external API calls
 * @property applicationProperties Application configuration properties
 * @see <a href="https://ipapi.co/api/">ipapi.co API Reference</a>
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
            }.mapCatching { json ->
                decodeJson<IpApiCoResponse>(json)
                    ?: throw IllegalStateException("Failed to decode GeoIP response: received empty or null response")
            }.onFailure { e ->
                logger.error("[GEO] Failed to parse response: ${e.message}", e)
            }.map { it.toDomain() }
}

/**
 * Converts ipapi.co API response to domain model.
 */
private fun IpApiCoResponse.toDomain(): GeoLocation =
    GeoLocation(
        ip = ip,
        version = version,
        city = city,
        region = region,
        regionCode = regionCode,
        countryCode = countryCode ?: country,
        countryCodeIso3 = countryCodeIso3,
        countryName = countryName,
        countryCapital = countryCapital,
        countryTld = countryTld,
        continentCode = continentCode,
        inEu = inEu,
        postal = postal,
        latitude = latitude,
        longitude = longitude,
        timezone = timezone,
        utcOffset = utcOffset,
        countryCallingCode = countryCallingCode,
        currency = currency,
        currencyName = currencyName,
        languages = languages,
        countryArea = countryArea,
        countryPopulation = countryPopulation,
        asn = asn,
        org = org,
        hostname = hostname,
        network = network,
        error = error,
        reason = reason,
        reserved = reserved,
    )
