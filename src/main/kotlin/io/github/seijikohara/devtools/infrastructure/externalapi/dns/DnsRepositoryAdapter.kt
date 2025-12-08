package io.github.seijikohara.devtools.infrastructure.externalapi.dns

import io.github.seijikohara.devtools.domain.dns.model.DnsQuestion
import io.github.seijikohara.devtools.domain.dns.model.DnsRecord
import io.github.seijikohara.devtools.domain.dns.model.DnsResolution
import io.github.seijikohara.devtools.domain.dns.model.Hostname
import io.github.seijikohara.devtools.domain.dns.repository.DnsRepository
import io.github.seijikohara.devtools.infrastructure.config.ApplicationProperties
import io.github.seijikohara.devtools.infrastructure.externalapi.common.decodeJson
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

/**
 * Adapter implementation for DNS resolution repository.
 *
 * Fetches DNS resolution data from Google DNS-over-HTTPS API and converts to domain model.
 *
 * @property webClient HTTP client for external API calls
 * @property applicationProperties Application configuration properties
 * @see <a href="https://developers.google.com/speed/public-dns/docs/doh/json">Google DNS-over-HTTPS JSON API</a>
 */
class DnsRepositoryAdapter(
    private val webClient: WebClient,
    private val applicationProperties: ApplicationProperties,
) : DnsRepository {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Resolves DNS records for a hostname.
     *
     * @param hostname The hostname to resolve
     * @param type The DNS record type (default: A record = 1)
     * @return [Result] containing [DnsResolution] on success, or failure with exception
     */
    override suspend fun invoke(
        hostname: Hostname,
        type: Int,
    ): Result<DnsResolution> =
        runCatching { "${applicationProperties.network.dns.uri}?name=${hostname.value}&type=$type" }
            .onSuccess { uri -> logger.info("[DNS] Fetching: $uri") }
            .mapCatching { uri ->
                webClient
                    .get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .awaitBody<String>()
            }.mapCatching { json ->
                decodeJson<GoogleDnsResponse>(json)
                    ?: throw IllegalStateException("Failed to decode DNS response: received empty or null response")
            }.onFailure { e ->
                logger.error("[DNS] Failed to parse response: ${e.message}", e)
            }.map { it.toDomain() }
}

/**
 * Converts Google DNS API response to domain model.
 */
private fun GoogleDnsResponse.toDomain(): DnsResolution =
    DnsResolution(
        status = status,
        truncated = tc,
        recursionDesired = rd,
        recursionAvailable = ra,
        authenticData = ad,
        checkingDisabled = cd,
        question = question?.map { it.toDomain() },
        answer = answer?.map { it.toDomain() },
        authority = authority?.map { it.toDomain() },
        additional = additional?.map { it.toDomain() },
        comment = comment,
        ednsClientSubnet = ednsClientSubnet,
    )

private fun io.github.seijikohara.devtools.infrastructure.externalapi.dns.DnsQuestion.toDomain(): DnsQuestion =
    DnsQuestion(
        name = name,
        type = type,
    )

private fun DnsAnswer.toDomain(): DnsRecord =
    DnsRecord(
        name = name,
        type = type,
        ttl = ttl,
        data = data,
    )

private fun DnsAuthority.toDomain(): DnsRecord =
    DnsRecord(
        name = name,
        type = type,
        ttl = ttl,
        data = data,
    )

private fun DnsAdditional.toDomain(): DnsRecord =
    DnsRecord(
        name = name,
        type = type,
        ttl = ttl,
        data = data,
    )
