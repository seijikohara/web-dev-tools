package io.github.seijikohara.devtools.infrastructure.web.controller

import io.github.seijikohara.devtools.application.usecase.ResolveDnsUseCase
import io.github.seijikohara.devtools.domain.dns.model.DnsRecordType
import io.github.seijikohara.devtools.infrastructure.web.dto.DnsResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

/**
 * REST controller for DNS resolution endpoint.
 *
 * Proxies DNS queries to Google DNS-over-HTTPS API and returns
 * the complete response in JSON format.
 *
 * @see <a href="https://developers.google.com/speed/public-dns/docs/doh/json">Google DNS-over-HTTPS JSON API</a>
 */
@RestController
@RequestMapping("\${application.api-base-path}")
@Tag(name = "DNS")
class DnsController(
    private val resolveDnsUseCase: ResolveDnsUseCase,
) {
    /**
     * Resolves DNS records for a hostname.
     *
     * Returns the complete Google DNS-over-HTTPS JSON API response including
     * status, question, answer, authority, and additional sections.
     *
     * @param hostname Hostname to resolve
     * @param type DNS record type (default: A record)
     * @return [DnsResponseDto] Complete DNS resolution response
     * @see <a href="https://www.iana.org/assignments/dns-parameters/dns-parameters.xhtml#dns-parameters-4">DNS RR Types</a>
     */
    @GetMapping("/dns/resolve/{hostname}")
    @Operation(summary = "Resolve DNS records for a hostname (Google DNS-over-HTTPS)")
    suspend fun resolveDns(
        @Parameter(description = "Hostname to resolve", example = "example.com")
        @PathVariable
        hostname: String,
        @Parameter(description = "DNS record type", example = "A")
        @RequestParam(defaultValue = "A")
        type: DnsRecordType,
    ): DnsResponseDto =
        resolveDnsUseCase(
            ResolveDnsUseCase.Request(hostname = hostname, type = type),
        ).fold(
            onSuccess = { it.dnsResolution },
            onFailure = { error ->
                when (error) {
                    is IllegalArgumentException ->
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, error.message)
                    else -> throw error
                }
            },
        )
}
