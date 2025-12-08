package io.github.seijikohara.devtools.infrastructure.externalapi.dns

import io.github.seijikohara.devtools.infrastructure.config.ApplicationProperties
import io.github.seijikohara.devtools.infrastructure.externalapi.common.decodeJson
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.springframework.core.io.ClassPathResource
import org.springframework.web.reactive.function.client.WebClient

/**
 * Unit tests for DnsRepositoryAdapter.
 *
 * Note: Due to the complexity of mocking WebClient's fluent API chain,
 * comprehensive testing of external API interactions is covered in integration tests.
 * These unit tests focus on verifying the adapter's structure and JSON parsing.
 *
 * See: DnsControllerIntegrationTest for end-to-end testing with actual (or mocked) DNS service.
 */
class DnsRepositoryAdapterSpec :
    FunSpec({

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
                                ipv4 = ClassPathResource("rdap/ipv4.json"),
                                ipv6 = ClassPathResource("rdap/ipv6.json"),
                            ),
                        geo =
                            ApplicationProperties.NetworkProperties.GeoProperties(
                                uri = "https://test.example.com",
                            ),
                        dns =
                            ApplicationProperties.NetworkProperties.DnsProperties(
                                uri = "https://dns.google/resolve",
                            ),
                    ),
            )

        context("DnsRepositoryAdapter initialization") {
            test("should successfully initialize with WebClient and ApplicationProperties") {
                val mockWebClient = mockk<WebClient>(relaxed = true)
                val applicationProperties = createTestApplicationProperties()

                val adapter = DnsRepositoryAdapter(mockWebClient, applicationProperties)

                adapter shouldBe adapter
            }
        }

        context("GoogleDnsResponse parsing") {
            test("should parse valid Google DNS JSON response with A record") {
                val testJson =
                    """
                    {
                        "Status": 0,
                        "TC": false,
                        "RD": true,
                        "RA": true,
                        "AD": false,
                        "CD": false,
                        "Question": [
                            {
                                "name": "example.com.",
                                "type": 1
                            }
                        ],
                        "Answer": [
                            {
                                "name": "example.com.",
                                "type": 1,
                                "TTL": 300,
                                "data": "93.184.216.34"
                            }
                        ]
                    }
                    """.trimIndent()

                val response = decodeJson<GoogleDnsResponse>(testJson)

                response?.status shouldBe 0
                response?.tc shouldBe false
                response?.rd shouldBe true
                response?.ra shouldBe true
                response?.ad shouldBe false
                response?.cd shouldBe false
                response?.question?.size shouldBe 1
                response?.question?.first()?.name shouldBe "example.com."
                response?.question?.first()?.type shouldBe 1
                response?.answer?.size shouldBe 1
                response?.answer?.first()?.name shouldBe "example.com."
                response?.answer?.first()?.type shouldBe 1
                response?.answer?.first()?.ttl shouldBe 300
                response?.answer?.first()?.data shouldBe "93.184.216.34"
            }

            test("should parse Google DNS response with CNAME and A records") {
                val testJson =
                    """
                    {
                        "Status": 0,
                        "TC": false,
                        "RD": true,
                        "RA": true,
                        "AD": false,
                        "CD": false,
                        "Question": [
                            {
                                "name": "www.example.com.",
                                "type": 1
                            }
                        ],
                        "Answer": [
                            {
                                "name": "www.example.com.",
                                "type": 5,
                                "TTL": 86400,
                                "data": "example.com."
                            },
                            {
                                "name": "example.com.",
                                "type": 1,
                                "TTL": 300,
                                "data": "93.184.216.34"
                            }
                        ]
                    }
                    """.trimIndent()

                val response = decodeJson<GoogleDnsResponse>(testJson)

                response?.answer?.size shouldBe 2
                response?.answer?.get(0)?.type shouldBe 5
                response?.answer?.get(0)?.data shouldBe "example.com."
                response?.answer?.get(1)?.type shouldBe 1
                response?.answer?.get(1)?.data shouldBe "93.184.216.34"
            }

            test("should parse Google DNS response with AAAA record") {
                val testJson =
                    """
                    {
                        "Status": 0,
                        "TC": false,
                        "RD": true,
                        "RA": true,
                        "AD": true,
                        "CD": false,
                        "Question": [
                            {
                                "name": "google.com.",
                                "type": 28
                            }
                        ],
                        "Answer": [
                            {
                                "name": "google.com.",
                                "type": 28,
                                "TTL": 300,
                                "data": "2607:f8b0:4004:800::200e"
                            }
                        ],
                        "edns_client_subnet": "0.0.0.0/0"
                    }
                    """.trimIndent()

                val response = decodeJson<GoogleDnsResponse>(testJson)

                response?.status shouldBe 0
                response?.ad shouldBe true
                response?.question?.first()?.type shouldBe 28
                response?.answer?.first()?.type shouldBe 28
                response?.answer?.first()?.data shouldBe "2607:f8b0:4004:800::200e"
                response?.ednsClientSubnet shouldBe "0.0.0.0/0"
            }

            test("should parse Google DNS NXDOMAIN response") {
                val testJson =
                    """
                    {
                        "Status": 3,
                        "TC": false,
                        "RD": true,
                        "RA": true,
                        "AD": false,
                        "CD": false,
                        "Question": [
                            {
                                "name": "nonexistent.example.invalid.",
                                "type": 1
                            }
                        ],
                        "Authority": [
                            {
                                "name": "example.invalid.",
                                "type": 6,
                                "TTL": 900,
                                "data": "ns1.example.invalid. hostmaster.example.invalid. 2024010101 7200 900 1209600 86400"
                            }
                        ],
                        "Comment": "Response from authoritative nameserver."
                    }
                    """.trimIndent()

                val response = decodeJson<GoogleDnsResponse>(testJson)

                response?.status shouldBe 3
                response?.answer shouldBe null
                response?.authority?.size shouldBe 1
                response?.authority?.first()?.type shouldBe 6
                response?.comment shouldBe "Response from authoritative nameserver."
            }

            test("should parse Google DNS MX record response") {
                val testJson =
                    """
                    {
                        "Status": 0,
                        "TC": false,
                        "RD": true,
                        "RA": true,
                        "AD": false,
                        "CD": false,
                        "Question": [
                            {
                                "name": "google.com.",
                                "type": 15
                            }
                        ],
                        "Answer": [
                            {
                                "name": "google.com.",
                                "type": 15,
                                "TTL": 600,
                                "data": "10 smtp.google.com."
                            },
                            {
                                "name": "google.com.",
                                "type": 15,
                                "TTL": 600,
                                "data": "20 smtp2.google.com."
                            }
                        ]
                    }
                    """.trimIndent()

                val response = decodeJson<GoogleDnsResponse>(testJson)

                response?.question?.first()?.type shouldBe 15
                response?.answer?.size shouldBe 2
                response?.answer?.get(0)?.data shouldBe "10 smtp.google.com."
                response?.answer?.get(1)?.data shouldBe "20 smtp2.google.com."
            }

            test("should parse minimal Google DNS response") {
                val testJson =
                    """
                    {
                        "Status": 0,
                        "TC": false,
                        "RD": true,
                        "RA": true,
                        "AD": false,
                        "CD": false
                    }
                    """.trimIndent()

                val response = decodeJson<GoogleDnsResponse>(testJson)

                response?.status shouldBe 0
                response?.question shouldBe null
                response?.answer shouldBe null
                response?.authority shouldBe null
                response?.additional shouldBe null
            }

            test("should parse Google DNS response with all sections") {
                val testJson =
                    """
                    {
                        "Status": 0,
                        "TC": false,
                        "RD": true,
                        "RA": true,
                        "AD": true,
                        "CD": false,
                        "Question": [
                            {
                                "name": "test.example.com.",
                                "type": 1
                            }
                        ],
                        "Answer": [
                            {
                                "name": "test.example.com.",
                                "type": 1,
                                "TTL": 300,
                                "data": "192.0.2.1"
                            }
                        ],
                        "Authority": [
                            {
                                "name": "example.com.",
                                "type": 2,
                                "TTL": 86400,
                                "data": "ns1.example.com."
                            }
                        ],
                        "Additional": [
                            {
                                "name": "ns1.example.com.",
                                "type": 1,
                                "TTL": 86400,
                                "data": "192.0.2.100"
                            }
                        ],
                        "Comment": "Test response",
                        "edns_client_subnet": "192.0.2.0/24"
                    }
                    """.trimIndent()

                val response = decodeJson<GoogleDnsResponse>(testJson)

                response?.status shouldBe 0
                response?.ad shouldBe true
                response?.question?.size shouldBe 1
                response?.answer?.size shouldBe 1
                response?.authority?.size shouldBe 1
                response?.additional?.size shouldBe 1
                response?.comment shouldBe "Test response"
                response?.ednsClientSubnet shouldBe "192.0.2.0/24"
            }
        }

        context("DnsResponseCode constants") {
            test("should have correct DNS response codes") {
                DnsResponseCode.NOERROR shouldBe 0
                DnsResponseCode.FORMERR shouldBe 1
                DnsResponseCode.SERVFAIL shouldBe 2
                DnsResponseCode.NXDOMAIN shouldBe 3
                DnsResponseCode.NOTIMP shouldBe 4
                DnsResponseCode.REFUSED shouldBe 5
            }
        }

        context("DnsRecordType constants") {
            test("should have correct DNS record type values") {
                DnsRecordType.A shouldBe 1
                DnsRecordType.NS shouldBe 2
                DnsRecordType.CNAME shouldBe 5
                DnsRecordType.SOA shouldBe 6
                DnsRecordType.PTR shouldBe 12
                DnsRecordType.MX shouldBe 15
                DnsRecordType.TXT shouldBe 16
                DnsRecordType.AAAA shouldBe 28
                DnsRecordType.SRV shouldBe 33
                DnsRecordType.ANY shouldBe 255
            }
        }
    })
