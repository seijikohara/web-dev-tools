package io.github.seijikohara.devtools.infrastructure.externalapi.rdap

import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapServerUri
import io.github.seijikohara.devtools.domain.networkinfo.model.buildQueryUri
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.net.URI

class RdapServerUriSpec :
    FunSpec({

        context("RdapServerUri.buildQueryUri() with IPv4 addresses") {
            data class Ipv4UriCase(
                val baseUri: String,
                val ipAddressStr: String,
                val expected: String,
            )

            withData(
                Ipv4UriCase("https://rdap.example.com", "192.168.1.1", "https://rdap.example.com/ip/192.168.1.1"),
                Ipv4UriCase("https://rdap.example.com/", "8.8.8.8", "https://rdap.example.com/ip/8.8.8.8"),
                Ipv4UriCase("https://rdap.example.com/api", "127.0.0.1", "https://rdap.example.com/ip/127.0.0.1"),
                Ipv4UriCase("https://rdap.example.com/api/", "10.0.0.1", "https://rdap.example.com/ip/10.0.0.1"),
            ) { (baseUri, ipAddressStr, expected) ->
                val rdapServerUri = RdapServerUri.of(URI.create(baseUri))
                val ipAddress = IpAddress.of(ipAddressStr).getOrThrow()

                val result = rdapServerUri.buildQueryUri(ipAddress)

                result.toString() shouldBe expected
            }
        }

        context("RdapServerUri.buildQueryUri() with IPv6 addresses") {
            data class Ipv6UriCase(
                val baseUri: String,
                val ipAddressStr: String,
                val expected: String,
            )

            withData(
                Ipv6UriCase("https://rdap.example.com", "2001:db8::1", "https://rdap.example.com/ip/2001:db8::1"),
                Ipv6UriCase("https://rdap.example.com/", "::1", "https://rdap.example.com/ip/::1"),
                Ipv6UriCase("https://rdap.example.com/api", "fe80::1", "https://rdap.example.com/ip/fe80::1"),
            ) { (baseUri, ipAddressStr, expected) ->
                val rdapServerUri = RdapServerUri.of(URI.create(baseUri))
                val ipAddress = IpAddress.of(ipAddressStr).getOrThrow()

                val result = rdapServerUri.buildQueryUri(ipAddress)

                result.toString() shouldBe expected
            }
        }

        context("RdapServerUri.buildQueryUri() with various base URIs") {
            val ipAddress = IpAddress.of("192.0.2.1").getOrThrow()

            data class BaseUriCase(
                val baseUri: String,
                val expected: String,
            )

            withData(
                BaseUriCase("https://rdap.arin.net/registry", "https://rdap.arin.net/ip/192.0.2.1"),
                BaseUriCase("https://rdap.apnic.net/", "https://rdap.apnic.net/ip/192.0.2.1"),
                BaseUriCase("http://rdap.example.com:8080/", "http://rdap.example.com:8080/ip/192.0.2.1"),
            ) { (baseUri, expected) ->
                val rdapServerUri = RdapServerUri.of(URI.create(baseUri))

                val result = rdapServerUri.buildQueryUri(ipAddress)

                result.toString() shouldBe expected
            }
        }

        context("URI path concatenation") {
            val ipAddress = IpAddress.of("192.0.2.1").getOrThrow()

            data class PathConcatCase(
                val baseUri: String,
                val expected: String,
                val description: String,
            )

            withData(
                PathConcatCase("https://example.com", "https://example.com/ip/192.0.2.1", "base without trailing slash"),
                PathConcatCase("https://example.com/", "https://example.com/ip/192.0.2.1", "base with trailing slash"),
                PathConcatCase("https://example.com/v1", "https://example.com/ip/192.0.2.1", "base with path"),
                PathConcatCase("https://example.com/v1/", "https://example.com/ip/192.0.2.1", "base with path and trailing slash"),
            ) { (baseUri, expected, _) ->
                val rdapServerUri = RdapServerUri.of(URI.create(baseUri))

                val result = rdapServerUri.buildQueryUri(ipAddress)

                result.toString() shouldBe expected
            }
        }
    })
