package io.github.seijikohara.devtools.domain.networkinfo.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class IpAddressSpec :
    FunSpec({

        context("IpAddress.of() with valid IPv4 addresses") {
            data class ValidIpV4Case(
                val address: String,
                val description: String,
            )

            withData(
                ValidIpV4Case("192.168.1.1", "typical private address"),
                ValidIpV4Case("8.8.8.8", "Google DNS"),
                ValidIpV4Case("127.0.0.1", "loopback address"),
                ValidIpV4Case("0.0.0.0", "all zeros"),
                ValidIpV4Case("255.255.255.255", "broadcast address"),
                ValidIpV4Case("10.0.0.1", "private network"),
            ) { (address, _) ->
                val result = IpAddress.of(address)
                result.isSuccess shouldBe true
                val ipAddress = result.getOrNull()!!
                ipAddress.value shouldBe address
                ipAddress.isIpV4() shouldBe true
                ipAddress.isIpV6() shouldBe false
            }
        }

        context("IpAddress.of() with valid IPv6 addresses") {
            data class ValidIpV6Case(
                val address: String,
                val description: String,
            )

            withData(
                ValidIpV6Case("2001:db8::1", "compressed format"),
                ValidIpV6Case("::1", "loopback address"),
                ValidIpV6Case("fe80::1", "link-local address"),
                ValidIpV6Case("2001:0db8:0000:0000:0000:0000:0000:0001", "full format"),
                ValidIpV6Case("::", "all zeros"),
                ValidIpV6Case("::ffff:192.168.1.1", "IPv4-mapped IPv6"),
            ) { (address, _) ->
                val result = IpAddress.of(address)
                result.isSuccess shouldBe true
                val ipAddress = result.getOrNull()!!
                ipAddress.isIpV6() shouldBe true
                ipAddress.isIpV4() shouldBe false
            }
        }

        context("IpAddress.of() with invalid addresses") {
            data class InvalidIpCase(
                val address: String,
                val description: String,
            )

            withData(
                InvalidIpCase("", "empty string"),
                InvalidIpCase("   ", "whitespace only"),
                InvalidIpCase("256.1.1.1", "IPv4 octet out of range"),
                InvalidIpCase("192.168.1.1.1", "too many IPv4 octets"),
                InvalidIpCase("not-an-ip", "plain text"),
                InvalidIpCase("gggg::1", "invalid IPv6 hex"),
            ) { (address, _) ->
                val result = IpAddress.of(address)
                result.isFailure shouldBe true
            }
        }

        context("IpAddress.of() error messages") {
            test("should contain appropriate error message for blank input") {
                val result = IpAddress.of("   ")
                result.isFailure shouldBe true
                result.exceptionOrNull()?.message shouldContain "cannot be blank"
            }

            test("should contain appropriate error message for invalid format") {
                val result = IpAddress.of("999.999.999.999")
                result.isFailure shouldBe true
                result.exceptionOrNull()?.message shouldContain "Invalid IP address format"
            }
        }

        context("IpAddress type checking") {
            context("should correctly identify IPv4 addresses") {
                withData(
                    "192.168.1.1",
                    "8.8.8.8",
                    "127.0.0.1",
                ) { address ->
                    val ipAddress = IpAddress.of(address).getOrThrow()
                    ipAddress.isIpV4() shouldBe true
                    ipAddress.isIpV6() shouldBe false
                }
            }

            context("should correctly identify IPv6 addresses") {
                withData(
                    "2001:db8::1",
                    "::1",
                    "fe80::1",
                ) { address ->
                    val ipAddress = IpAddress.of(address).getOrThrow()
                    ipAddress.isIpV6() shouldBe true
                    ipAddress.isIpV4() shouldBe false
                }
            }
        }

        context("IpAddress.toInetIPAddress()") {
            data class ConversionCase(
                val address: String,
                val description: String,
            )

            withData(
                ConversionCase("192.168.1.1", "IPv4"),
                ConversionCase("2001:db8::1", "IPv6"),
            ) { (address, _) ->
                val ipAddress = IpAddress.of(address).getOrThrow()
                val inetAddress = ipAddress.toInetIPAddress()
                inetAddress shouldNotBe null
                inetAddress.toString() shouldBe address
            }
        }

        context("IpAddress.toString()") {
            withData(
                "192.168.1.1",
                "2001:db8::1",
                "::1",
            ) { address ->
                val ipAddress = IpAddress.of(address).getOrThrow()
                ipAddress.toString() shouldBe address
            }
        }
    })
