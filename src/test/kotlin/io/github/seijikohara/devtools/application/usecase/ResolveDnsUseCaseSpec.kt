package io.github.seijikohara.devtools.application.usecase

import io.github.seijikohara.devtools.domain.dns.model.DnsQuestion
import io.github.seijikohara.devtools.domain.dns.model.DnsRecord
import io.github.seijikohara.devtools.domain.dns.model.DnsRecordType
import io.github.seijikohara.devtools.domain.dns.model.DnsResolution
import io.github.seijikohara.devtools.domain.dns.model.Hostname
import io.github.seijikohara.devtools.domain.dns.repository.DnsRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class ResolveDnsUseCaseSpec :
    FunSpec({

        context("ResolveDnsUseCase execution") {
            data class DnsResolveTestCase(
                val hostname: String,
                val type: DnsRecordType,
                val expectedIp: String,
            )

            withData(
                DnsResolveTestCase("example.com", DnsRecordType.A, "93.184.216.34"),
                DnsResolveTestCase("google.com", DnsRecordType.A, "142.250.190.14"),
                DnsResolveTestCase("cloudflare.com", DnsRecordType.AAAA, "2606:4700::6811:d166"),
            ) { (hostnameStr, type, expectedIp) ->
                val mockRepository = mockk<DnsRepository>()
                val hostname = Hostname.of(hostnameStr).getOrThrow()
                val expectedResolution =
                    DnsResolution(
                        status = 0,
                        truncated = false,
                        recursionDesired = true,
                        recursionAvailable = true,
                        authenticData = false,
                        checkingDisabled = false,
                        question =
                            listOf(
                                DnsQuestion(
                                    name = "$hostnameStr.",
                                    type = type.value,
                                ),
                            ),
                        answer =
                            listOf(
                                DnsRecord(
                                    name = "$hostnameStr.",
                                    type = type.value,
                                    ttl = 300,
                                    data = expectedIp,
                                ),
                            ),
                    )

                coEvery { mockRepository(hostname, type.value) } returns Result.success(expectedResolution)

                val useCase = resolveDnsUseCase(mockRepository)
                val request = ResolveDnsUseCase.Request(hostname = hostnameStr, type = type)

                val result = useCase(request)

                result.isSuccess shouldBe true
                val response = result.getOrNull()!!
                response.dnsResolution.status shouldBe 0
                response.dnsResolution.question
                    ?.first()
                    ?.name shouldBe "$hostnameStr."
                response.dnsResolution.answer
                    ?.first()
                    ?.data shouldBe expectedIp

                coVerify(exactly = 1) { mockRepository(hostname, type.value) }
            }
        }

        context("ResolveDnsUseCase with invalid hostname") {
            data class InvalidHostnameCase(
                val invalidHostname: String,
                val description: String,
            )

            withData(
                InvalidHostnameCase("", "empty string"),
                InvalidHostnameCase("   ", "whitespace"),
                InvalidHostnameCase("-example.com", "starts with hyphen"),
                InvalidHostnameCase("example-.com", "ends with hyphen"),
                InvalidHostnameCase("exam_ple.com", "contains underscore"),
            ) { (invalidHostname, _) ->
                val mockRepository = mockk<DnsRepository>()
                val useCase = resolveDnsUseCase(mockRepository)
                val request = ResolveDnsUseCase.Request(hostname = invalidHostname, type = DnsRecordType.A)

                val result = useCase(request)

                result.isFailure shouldBe true
                result.exceptionOrNull() shouldNotBe null

                coVerify(exactly = 0) { mockRepository(any(), any()) }
            }
        }

        context("ResolveDnsUseCase with repository errors") {
            test("should propagate repository errors") {
                val mockRepository = mockk<DnsRepository>()
                val hostname = Hostname.of("test.example.com").getOrThrow()
                val repositoryError = RuntimeException("Network error")

                coEvery { mockRepository(hostname, DnsRecordType.A.value) } returns Result.failure(repositoryError)

                val useCase = resolveDnsUseCase(mockRepository)
                val request = ResolveDnsUseCase.Request(hostname = "test.example.com", type = DnsRecordType.A)

                val result = useCase(request)

                result.isFailure shouldBe true
                result.exceptionOrNull() shouldBe repositoryError

                coVerify(exactly = 1) { mockRepository(hostname, DnsRecordType.A.value) }
            }

            test("should handle NXDOMAIN response") {
                val mockRepository = mockk<DnsRepository>()
                val hostname = Hostname.of("nonexistent.example.invalid").getOrThrow()
                val nxdomainResolution =
                    DnsResolution(
                        status = 3,
                        truncated = false,
                        recursionDesired = true,
                        recursionAvailable = true,
                        authenticData = false,
                        checkingDisabled = false,
                        question =
                            listOf(
                                DnsQuestion(
                                    name = "nonexistent.example.invalid.",
                                    type = DnsRecordType.A.value,
                                ),
                            ),
                        answer = null,
                        comment = "NXDOMAIN",
                    )

                coEvery { mockRepository(hostname, DnsRecordType.A.value) } returns Result.success(nxdomainResolution)

                val useCase = resolveDnsUseCase(mockRepository)
                val request = ResolveDnsUseCase.Request(hostname = "nonexistent.example.invalid", type = DnsRecordType.A)

                val result = useCase(request)

                result.isSuccess shouldBe true
                val response = result.getOrNull()!!
                response.dnsResolution.status shouldBe 3
                response.dnsResolution.answer shouldBe null
            }
        }

        context("ResolveDnsUseCase with different record types") {
            withData(
                DnsRecordType.A,
                DnsRecordType.AAAA,
                DnsRecordType.CNAME,
                DnsRecordType.MX,
                DnsRecordType.TXT,
                DnsRecordType.NS,
            ) { type ->
                val mockRepository = mockk<DnsRepository>()
                val hostname = Hostname.of("example.com").getOrThrow()
                val resolution =
                    DnsResolution(
                        status = 0,
                        truncated = false,
                        recursionDesired = true,
                        recursionAvailable = true,
                        authenticData = false,
                        checkingDisabled = false,
                        question =
                            listOf(
                                DnsQuestion(name = "example.com.", type = type.value),
                            ),
                    )

                coEvery { mockRepository(hostname, type.value) } returns Result.success(resolution)

                val useCase = resolveDnsUseCase(mockRepository)
                val request = ResolveDnsUseCase.Request(hostname = "example.com", type = type)

                val result = useCase(request)

                result.isSuccess shouldBe true
                result
                    .getOrNull()!!
                    .dnsResolution.question
                    ?.first()
                    ?.type shouldBe type.value

                coVerify(exactly = 1) { mockRepository(hostname, type.value) }
            }
        }
    })
