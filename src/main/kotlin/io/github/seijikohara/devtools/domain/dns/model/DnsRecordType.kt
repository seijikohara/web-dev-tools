package io.github.seijikohara.devtools.domain.dns.model

/**
 * DNS resource record types.
 *
 * Based on IANA DNS Resource Record Types registry.
 *
 * @property value The numeric type code used in DNS wire format
 * @see <a href="https://www.iana.org/assignments/dns-parameters/dns-parameters.xhtml#dns-parameters-4">IANA DNS RR Types</a>
 */
enum class DnsRecordType(
    val value: Int,
) {
    /** IPv4 address record */
    A(1),

    /** Name server record */
    NS(2),

    /** Canonical name (alias) record */
    CNAME(5),

    /** Start of authority record */
    SOA(6),

    /** Pointer record (reverse DNS) */
    PTR(12),

    /** Mail exchange record */
    MX(15),

    /** Text record */
    TXT(16),

    /** IPv6 address record */
    AAAA(28),

    /** Service locator record */
    SRV(33),

    /** DNSSEC delegation signer */
    DS(43),

    /** DNSSEC resource record signature */
    RRSIG(46),

    /** DNSSEC next secure record */
    NSEC(47),

    /** DNSSEC key record */
    DNSKEY(48),

    /** Certificate Association record */
    TLSA(52),

    /** HTTPS service binding */
    HTTPS(65),

    /** Request all records */
    ANY(255),
    ;

    companion object {
        private val valueMap = entries.associateBy { it.value }

        /**
         * Gets DnsRecordType by its numeric value.
         *
         * @param value The numeric type code
         * @return The corresponding DnsRecordType, or null if not found
         */
        fun fromValue(value: Int): DnsRecordType? = valueMap[value]
    }
}
