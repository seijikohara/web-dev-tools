package io.github.seijikohara.devtools.infrastructure.externalapi.dns

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Google DNS-over-HTTPS JSON API response.
 *
 * Represents the complete response from Google's Public DNS JSON API.
 *
 * @property status Standard DNS response code (RCODE). 0=NOERROR, 2=SERVFAIL, 3=NXDOMAIN, etc.
 * @property tc Whether the response was truncated (always false for JSON API)
 * @property rd Recursion Desired flag (always true for Google Public DNS)
 * @property ra Recursion Available flag (always true for Google Public DNS)
 * @property ad Whether all response data was validated with DNSSEC
 * @property cd Whether the client asked to disable DNSSEC validation
 * @property question The question section containing the query details
 * @property answer The answer section containing DNS resource records
 * @property authority The authority section (for referrals)
 * @property additional The additional section
 * @property comment Diagnostic information or nameserver attribution
 * @property ednsClientSubnet The EDNS Client Subnet used in the query
 *
 * @see <a href="https://developers.google.com/speed/public-dns/docs/doh/json">Google DNS-over-HTTPS JSON API</a>
 */
@Serializable
data class GoogleDnsResponse(
    @SerialName("Status") val status: Int,
    @SerialName("TC") val tc: Boolean,
    @SerialName("RD") val rd: Boolean,
    @SerialName("RA") val ra: Boolean,
    @SerialName("AD") val ad: Boolean,
    @SerialName("CD") val cd: Boolean,
    @SerialName("Question") val question: List<DnsQuestion>? = null,
    @SerialName("Answer") val answer: List<DnsAnswer>? = null,
    @SerialName("Authority") val authority: List<DnsAuthority>? = null,
    @SerialName("Additional") val additional: List<DnsAdditional>? = null,
    @SerialName("Comment") val comment: String? = null,
    @SerialName("edns_client_subnet") val ednsClientSubnet: String? = null,
)

/**
 * DNS question section entry.
 *
 * @property name The queried domain name (FQDN with trailing dot)
 * @property type The DNS resource record type (1=A, 28=AAAA, etc.)
 */
@Serializable
data class DnsQuestion(
    val name: String,
    val type: Int,
)

/**
 * DNS answer section entry.
 *
 * @property name The domain name this record applies to
 * @property type The DNS resource record type
 * @property ttl Time-to-live in seconds
 * @property data The record data (IP address for A/AAAA, domain for CNAME, etc.)
 */
@Serializable
data class DnsAnswer(
    val name: String,
    val type: Int,
    @SerialName("TTL") val ttl: Int,
    val data: String,
)

/**
 * DNS authority section entry.
 *
 * @property name The domain name this record applies to
 * @property type The DNS resource record type
 * @property ttl Time-to-live in seconds
 * @property data The record data
 */
@Serializable
data class DnsAuthority(
    val name: String,
    val type: Int,
    @SerialName("TTL") val ttl: Int,
    val data: String,
)

/**
 * DNS additional section entry.
 *
 * @property name The domain name this record applies to
 * @property type The DNS resource record type
 * @property ttl Time-to-live in seconds
 * @property data The record data
 */
@Serializable
data class DnsAdditional(
    val name: String,
    val type: Int,
    @SerialName("TTL") val ttl: Int,
    val data: String,
)

/**
 * Standard DNS response codes (RCODE).
 *
 * @see <a href="https://www.iana.org/assignments/dns-parameters/dns-parameters.xhtml#dns-parameters-6">IANA DNS RCODEs</a>
 */
object DnsResponseCode {
    /** No error condition */
    const val NOERROR = 0

    /** Format error - unable to interpret query */
    const val FORMERR = 1

    /** Server failure - unable to process query */
    const val SERVFAIL = 2

    /** Name error - domain name does not exist */
    const val NXDOMAIN = 3

    /** Not implemented - query type not supported */
    const val NOTIMP = 4

    /** Refused - server refuses to perform operation */
    const val REFUSED = 5
}

/**
 * Common DNS resource record types.
 *
 * @see <a href="https://www.iana.org/assignments/dns-parameters/dns-parameters.xhtml#dns-parameters-4">IANA DNS RR Types</a>
 */
object DnsRecordType {
    /** IPv4 address */
    const val A = 1

    /** Name server */
    const val NS = 2

    /** Canonical name (alias) */
    const val CNAME = 5

    /** Start of authority */
    const val SOA = 6

    /** Pointer record */
    const val PTR = 12

    /** Mail exchange */
    const val MX = 15

    /** Text record */
    const val TXT = 16

    /** IPv6 address */
    const val AAAA = 28

    /** Service locator */
    const val SRV = 33

    /** All records */
    const val ANY = 255
}
