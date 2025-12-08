package io.github.seijikohara.devtools.domain.dns.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

class HostnameSpec :
    FunSpec({

        context("Hostname creation with valid hostnames") {
            data class ValidHostnameCase(
                val input: String,
                val expected: String,
                val description: String,
            )

            withData(
                ValidHostnameCase("example.com", "example.com", "simple domain"),
                ValidHostnameCase("www.example.com", "www.example.com", "subdomain"),
                ValidHostnameCase("sub.domain.example.com", "sub.domain.example.com", "multiple subdomains"),
                ValidHostnameCase("EXAMPLE.COM", "example.com", "uppercase normalized to lowercase"),
                ValidHostnameCase("  example.com  ", "example.com", "trimmed whitespace"),
                ValidHostnameCase("a.co", "a.co", "short domain"),
                ValidHostnameCase("xn--nxasmq5b.com", "xn--nxasmq5b.com", "punycode domain"),
                ValidHostnameCase("123.example.com", "123.example.com", "numeric subdomain"),
                ValidHostnameCase("my-server.example.com", "my-server.example.com", "hyphenated subdomain"),
                ValidHostnameCase("a1.b2.c3.example.com", "a1.b2.c3.example.com", "alphanumeric labels"),
            ) { (input, expected, _) ->
                val result = Hostname.of(input)

                result.isSuccess shouldBe true
                result.getOrNull()!!.value shouldBe expected
            }
        }

        context("Hostname creation with invalid hostnames") {
            data class InvalidHostnameCase(
                val input: String,
                val description: String,
            )

            withData(
                InvalidHostnameCase("", "empty string"),
                InvalidHostnameCase("   ", "whitespace only"),
                InvalidHostnameCase("-example.com", "label starts with hyphen"),
                InvalidHostnameCase("example-.com", "label ends with hyphen"),
                InvalidHostnameCase(".example.com", "starts with dot"),
                InvalidHostnameCase("example.com.", "ends with dot"),
                InvalidHostnameCase("example..com", "consecutive dots"),
                InvalidHostnameCase("exam_ple.com", "underscore in label"),
                InvalidHostnameCase("exam ple.com", "space in label"),
                InvalidHostnameCase(
                    "a".repeat(64) + ".com",
                    "label exceeds 63 characters",
                ),
                InvalidHostnameCase(
                    ("a".repeat(63) + ".").repeat(4) + "com",
                    "hostname exceeds 253 characters",
                ),
            ) { (input, _) ->
                val result = Hostname.of(input)

                result.isFailure shouldBe true
                result.exceptionOrNull() shouldNotBe null
                result.exceptionOrNull().shouldBeInstanceOf<IllegalArgumentException>()
            }
        }

        context("Hostname value class behavior") {
            test("should be equal when values are the same") {
                val hostname1 = Hostname.of("example.com").getOrThrow()
                val hostname2 = Hostname.of("EXAMPLE.COM").getOrThrow()

                hostname1.value shouldBe hostname2.value
            }

            test("should expose value property") {
                val hostname = Hostname.of("test.example.com").getOrThrow()

                hostname.value shouldBe "test.example.com"
            }
        }
    })
