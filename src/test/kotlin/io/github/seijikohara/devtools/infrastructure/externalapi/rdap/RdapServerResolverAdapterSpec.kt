package io.github.seijikohara.devtools.infrastructure.externalapi.rdap

import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.repository.RdapServerNotFoundException
import io.github.seijikohara.devtools.infrastructure.config.ApplicationProperties
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.springframework.core.io.ClassPathResource
import java.net.URI

class RdapServerResolverAdapterSpec :
    FunSpec({

        /**
         * Creates test ApplicationProperties with test RDAP files.
         */
        fun createTestApplicationProperties(): ApplicationProperties =
            ApplicationProperties(
                apiBasePath = "/api",
                indexFile = ClassPathResource("static/index.html"),
                swaggerUiPaths = listOf(),
                cors =
                    ApplicationProperties.CorsProperties(
                        mappingPathPattern = "/api/**",
                        allowedOrigins = listOf("*"),
                        allowedMethods = listOf("*"),
                        maxAge = 3600,
                    ),
                network =
                    ApplicationProperties.NetworkProperties(
                        rdap =
                            ApplicationProperties.NetworkProperties.RdapProperties(
                                ipv4 = ClassPathResource("rdap/test-ipv4.json"),
                                ipv6 = ClassPathResource("rdap/test-ipv6.json"),
                            ),
                        geo =
                            ApplicationProperties.NetworkProperties.GeoProperties(
                                uri = "https://ipapi.co/",
                            ),
                        dns =
                            ApplicationProperties.NetworkProperties.DnsProperties(
                                uri = "https://dns.google/resolve",
                            ),
                    ),
            )

        context("RdapServerResolverAdapter resolution") {
            data class SuccessfulResolutionTestCase(
                val description: String,
                val ipAddressString: String,
                val expectedRdapUri: String,
            )

            withData(
                nameFn = { it.description },
                SuccessfulResolutionTestCase(
                    description = "should resolve IPv4 address in 1.0.0.0/8 range",
                    ipAddressString = "1.2.3.4",
                    expectedRdapUri = "https://rdap.example.com/test1/",
                ),
                SuccessfulResolutionTestCase(
                    description = "should resolve IPv4 address in 2.0.0.0/8 range",
                    ipAddressString = "2.255.255.255",
                    expectedRdapUri = "https://rdap.example.com/test1/",
                ),
                SuccessfulResolutionTestCase(
                    description = "should resolve IPv4 address in 8.8.8.0/24 range",
                    ipAddressString = "8.8.8.8",
                    expectedRdapUri = "https://rdap.example.com/test2/",
                ),
                SuccessfulResolutionTestCase(
                    description = "should resolve IPv4 address in 192.168.0.0/16 range",
                    ipAddressString = "192.168.1.1",
                    expectedRdapUri = "https://rdap.example.com/test3/",
                ),
                SuccessfulResolutionTestCase(
                    description = "should resolve IPv6 address in 2001:db8::/32 range",
                    ipAddressString = "2001:db8::1",
                    expectedRdapUri = "https://rdap.example.com/testv6/",
                ),
                SuccessfulResolutionTestCase(
                    description = "should resolve IPv6 address in 2400::/12 range",
                    ipAddressString = "2400::1",
                    expectedRdapUri = "https://rdap.example.com/testv6-2/",
                ),
            ) { (_, ipAddressString, expectedRdapUri) ->
                val applicationProperties = createTestApplicationProperties()
                val resolver = RdapServerResolverAdapter(applicationProperties)
                val ipAddress = IpAddress.of(ipAddressString).getOrThrow()

                val result = resolver(ipAddress)

                assertSoftly(result) {
                    it.shouldBeSuccess()
                    it.getOrThrow().value shouldBe URI.create(expectedRdapUri)
                }
            }

            data class BoundaryTestCase(
                val description: String,
                val ipAddressString: String,
                val expectedRdapUri: String,
            )

            withData(
                nameFn = { it.description },
                BoundaryTestCase(
                    description = "should resolve IPv4 address at lower bound of 1.0.0.0/8",
                    ipAddressString = "1.0.0.0",
                    expectedRdapUri = "https://rdap.example.com/test1/",
                ),
                BoundaryTestCase(
                    description = "should resolve IPv4 address at upper bound of 1.0.0.0/8",
                    ipAddressString = "1.255.255.255",
                    expectedRdapUri = "https://rdap.example.com/test1/",
                ),
                BoundaryTestCase(
                    description = "should resolve IPv4 address at lower bound of 8.8.8.0/24",
                    ipAddressString = "8.8.8.0",
                    expectedRdapUri = "https://rdap.example.com/test2/",
                ),
                BoundaryTestCase(
                    description = "should resolve IPv4 address at upper bound of 8.8.8.0/24",
                    ipAddressString = "8.8.8.255",
                    expectedRdapUri = "https://rdap.example.com/test2/",
                ),
            ) { (_, ipAddressString, expectedRdapUri) ->
                val applicationProperties = createTestApplicationProperties()
                val resolver = RdapServerResolverAdapter(applicationProperties)
                val ipAddress = IpAddress.of(ipAddressString).getOrThrow()

                val result = resolver(ipAddress)

                assertSoftly(result) {
                    it.shouldBeSuccess()
                    it.getOrThrow().value shouldBe URI.create(expectedRdapUri)
                }
            }

            data class FailureTestCase(
                val description: String,
                val ipAddressString: String,
            )

            withData(
                nameFn = { it.description },
                FailureTestCase(
                    description = "should throw RdapServerNotFoundException for IPv4 address not in any range",
                    ipAddressString = "3.0.0.1",
                ),
                FailureTestCase(
                    description = "should throw RdapServerNotFoundException for IPv4 address outside defined ranges",
                    ipAddressString = "10.0.0.1",
                ),
                FailureTestCase(
                    description = "should throw RdapServerNotFoundException for IPv4 address just outside 8.8.8.0/24",
                    ipAddressString = "8.8.9.0",
                ),
                FailureTestCase(
                    description = "should throw RdapServerNotFoundException for IPv6 address not in any range",
                    ipAddressString = "2002::1",
                ),
            ) { (_, ipAddressString) ->
                val applicationProperties = createTestApplicationProperties()
                val resolver = RdapServerResolverAdapter(applicationProperties)
                val ipAddress = IpAddress.of(ipAddressString).getOrThrow()

                val result = resolver(ipAddress)

                assertSoftly(result) {
                    it.shouldBeFailure()
                    it.exceptionOrNull().shouldBeInstanceOf<RdapServerNotFoundException>()
                    it.exceptionOrNull()?.message shouldBe "No RDAP server found for $ipAddressString"
                }
            }
        }
    })
