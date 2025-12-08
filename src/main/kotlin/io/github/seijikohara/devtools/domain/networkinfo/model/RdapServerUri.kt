@file:Suppress("ktlint:standard:no-consecutive-comments")

package io.github.seijikohara.devtools.domain.networkinfo.model

import java.net.URI

/**
 * Represents the base URI of an RDAP server.
 *
 * Type-safe wrapper for RDAP server URIs that enforces proper usage of RDAP-specific operations.
 *
 * @property value The base URI of the RDAP server
 * @see buildQueryUri
 */
@JvmInline
value class RdapServerUri internal constructor(
    val value: URI,
) {
    companion object {
        /**
         * Creates [RdapServerUri] from [URI].
         *
         * @param uri The base URI of the RDAP server
         * @return [RdapServerUri] instance
         */
        fun of(uri: URI): RdapServerUri = RdapServerUri(uri)
    }
}

/**
 * Builds RDAP query URI for IP address lookup according to RFC 7482.
 *
 * Constructs the complete RDAP query URI by appending the IP address path segment.
 * The query path replaces any existing path in the base server URI.
 *
 * @receiver The RDAP server base URI
 * @param ipAddress The IP address to query
 * @return Complete RDAP query URI
 * @see IpAddress
 */
fun RdapServerUri.buildQueryUri(ipAddress: IpAddress): URI = value.resolve("ip/${ipAddress.value}")
