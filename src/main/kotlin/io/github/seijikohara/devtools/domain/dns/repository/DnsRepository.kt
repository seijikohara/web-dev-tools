package io.github.seijikohara.devtools.domain.dns.repository

import io.github.seijikohara.devtools.domain.dns.model.DnsResolution
import io.github.seijikohara.devtools.domain.dns.model.Hostname

/**
 * Repository interface for DNS resolution.
 *
 * Provides access to DNS resolution services.
 * Returns the complete response as a domain model [DnsResolution].
 *
 * @see DnsResolution
 * @see Hostname
 */
fun interface DnsRepository {
    /**
     * Resolves DNS records for a hostname.
     *
     * @param hostname The hostname to resolve
     * @param type The DNS record type (1=A, 28=AAAA, 5=CNAME, 15=MX, 16=TXT, etc.)
     * @return [Result] containing [DnsResolution] on success, or failure with exception
     */
    suspend operator fun invoke(
        hostname: Hostname,
        type: Int,
    ): Result<DnsResolution>
}
