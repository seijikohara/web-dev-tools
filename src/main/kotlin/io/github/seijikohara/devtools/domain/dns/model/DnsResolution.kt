@file:Suppress("ktlint:standard:max-line-length")

package io.github.seijikohara.devtools.domain.dns.model

/**
 * Represents complete DNS resolution information.
 *
 * This domain model encompasses all DNS resolution data that can be obtained from DNS queries.
 * Based on RFC 1035 and Google DNS-over-HTTPS JSON API response format.
 *
 * @property status Standard DNS response code (RCODE). 0=NOERROR, 2=SERVFAIL, 3=NXDOMAIN, etc.
 * @property truncated Whether the response was truncated
 * @property recursionDesired Recursion Desired flag
 * @property recursionAvailable Recursion Available flag
 * @property authenticData Whether all response data was validated with DNSSEC
 * @property checkingDisabled Whether DNSSEC validation was disabled
 * @property question The question section containing the query details
 * @property answer The answer section containing DNS resource records
 * @property authority The authority section (for referrals)
 * @property additional The additional section
 * @property comment Diagnostic information or nameserver attribution
 * @property ednsClientSubnet The EDNS Client Subnet used in the query
 *
 * @see DnsQuestion
 * @see DnsRecord
 * @see <a href="https://www.rfc-editor.org/rfc/rfc1035">RFC 1035</a>
 * @see <a href="https://developers.google.com/speed/public-dns/docs/doh/json">Google DNS-over-HTTPS JSON API</a>
 */
data class DnsResolution(
    val status: Int,
    val truncated: Boolean,
    val recursionDesired: Boolean,
    val recursionAvailable: Boolean,
    val authenticData: Boolean,
    val checkingDisabled: Boolean,
    val question: List<DnsQuestion>? = null,
    val answer: List<DnsRecord>? = null,
    val authority: List<DnsRecord>? = null,
    val additional: List<DnsRecord>? = null,
    val comment: String? = null,
    val ednsClientSubnet: String? = null,
)

/**
 * DNS question entry.
 *
 * @property name The queried domain name
 * @property type The DNS resource record type code
 */
data class DnsQuestion(
    val name: String,
    val type: Int,
)

/**
 * DNS resource record.
 *
 * @property name The domain name this record applies to
 * @property type The DNS resource record type code
 * @property ttl Time-to-live in seconds
 * @property data The record data (IP address for A/AAAA, domain for CNAME, etc.)
 */
data class DnsRecord(
    val name: String,
    val type: Int,
    val ttl: Int,
    val data: String,
)
