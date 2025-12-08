package io.github.seijikohara.devtools.application.usecase

import io.github.seijikohara.devtools.domain.common.extensions.flatMap
import io.github.seijikohara.devtools.domain.dns.model.DnsRecordType
import io.github.seijikohara.devtools.domain.dns.model.DnsResolution
import io.github.seijikohara.devtools.domain.dns.model.Hostname
import io.github.seijikohara.devtools.domain.dns.repository.DnsRepository

/**
 * Use case for resolving DNS records for a hostname.
 *
 * Returns the complete DNS resolution data with all available fields.
 *
 * @see Request
 * @see Response
 * @see DnsResolution
 * @see <a href="https://developers.google.com/speed/public-dns/docs/doh/json">Google DNS-over-HTTPS JSON API</a>
 */
fun interface ResolveDnsUseCase {
    /**
     * Resolves DNS records for a hostname.
     *
     * @param request The request containing the hostname string and record type
     * @return [Result] containing [Response] on success, or failure with exception
     */
    suspend operator fun invoke(request: Request): Result<Response>

    /**
     * Request for resolving DNS records.
     *
     * @property hostname Hostname to resolve
     * @property type DNS record type (default: A record)
     */
    data class Request(
        val hostname: String,
        val type: DnsRecordType = DnsRecordType.A,
    )

    /**
     * Response containing DNS resolution information.
     *
     * @property dnsResolution Complete DNS resolution data
     */
    data class Response(
        val dnsResolution: DnsResolution,
    )
}

/**
 * Creates a [ResolveDnsUseCase] instance.
 *
 * @param dnsRepository The DNS repository implementation
 * @return A [ResolveDnsUseCase] instance
 */
fun resolveDnsUseCase(dnsRepository: DnsRepository): ResolveDnsUseCase =
    ResolveDnsUseCase { request ->
        Hostname
            .of(request.hostname)
            .flatMap { hostname ->
                dnsRepository(hostname, request.type.value)
            }.map { dnsResolution ->
                ResolveDnsUseCase.Response(dnsResolution)
            }
    }
