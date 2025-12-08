package io.github.seijikohara.devtools.infrastructure.web.dto

import io.github.seijikohara.devtools.domain.dns.model.DnsResolution

/**
 * Type alias for DNS resolution response DTO.
 *
 * Returns the complete DNS resolution data directly.
 *
 * @see DnsResolution
 * @see <a href="https://developers.google.com/speed/public-dns/docs/doh/json">Google DNS-over-HTTPS JSON API</a>
 */
typealias DnsResponseDto = DnsResolution
