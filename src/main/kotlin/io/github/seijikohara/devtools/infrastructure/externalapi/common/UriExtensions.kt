package io.github.seijikohara.devtools.infrastructure.externalapi.common

import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import java.net.URI

/**
 * Builds RDAP URI by resolving path segments for IP lookup.
 *
 * @receiver The base RDAP server URI
 * @param ipAddress The IP address to query
 * @return Complete RDAP query URI
 */
fun URI.buildRdapUri(ipAddress: IpAddress): URI = resolve(concatenatePaths("/", "ip", ipAddress.value))

/**
 * Concatenates multiple path segments into a single path string.
 *
 * Handles leading and trailing slashes appropriately, normalizes multiple
 * slashes, and removes empty segments.
 *
 * @param paths Variable number of path segments
 * @return Normalized path string
 */
private fun concatenatePaths(vararg paths: String): String =
    paths
        .takeIf { it.isNotEmpty() }
        ?.let { pathArray ->
            Triple(
                pathArray.first().startsWith('/'),
                pathArray.last().endsWith('/'),
                pathArray
                    .asSequence()
                    .map { it.replace(Regex("/+"), "/") }
                    .filter(String::isNotEmpty)
                    .map { it.trim('/') }
                    .filter(String::isNotEmpty)
                    .toList()
                    .takeIf(List<String>::isNotEmpty)
                    ?.joinToString("/"),
            ).let { (hasLeadingSlash, hasTrailingSlash, joinedPath) ->
                joinedPath?.let { path ->
                    buildList {
                        if (hasLeadingSlash) add("/")
                        add(path)
                        if (hasTrailingSlash) add("/")
                    }.joinToString("")
                } ?: if (hasLeadingSlash) "/" else ""
            }
        } ?: ""
