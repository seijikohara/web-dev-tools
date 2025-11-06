package io.github.seijikohara.devtools.application.usecase

import io.github.seijikohara.devtools.domain.networkinfo.model.CountryCode
import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapInformation
import io.github.seijikohara.devtools.domain.networkinfo.repository.RdapRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.serialization.json.JsonElement

class GetRdapInformationUseCaseSpec :
    FunSpec({

        context("GetRdapInformationUseCase execution") {
            data class RdapTestCase(
                val ipStr: String,
                val handle: String,
                val name: String,
                val countryStr: String,
            )

            withData(
                RdapTestCase("192.0.2.1", "NET-192-0-2-0-1", "TEST-NET-1", "US"),
                RdapTestCase("8.8.8.8", "NET-8-8-8-0-1", "GOOGLE", "US"),
                RdapTestCase("2001:db8::1", "NET6-2001-DB8-1", "EXAMPLE-NET", "EU"),
            ) { (ipStr, handle, name, countryStr) ->
                val mockRepository = mockk<RdapRepository>()
                val ipAddress = IpAddress.of(ipStr).getOrThrow()
                val expectedRdapInfo =
                    RdapInformation(
                        ipAddress = ipAddress,
                        handle = handle,
                        name = name,
                        country = CountryCode.of(countryStr).getOrNull(),
                        registeredAt = null,
                        rawData = emptyMap<String, JsonElement>(),
                    )

                coEvery { mockRepository(ipAddress) } returns Result.success(expectedRdapInfo)

                val useCase = getRdapInformationUseCase(mockRepository)
                val request = GetRdapInformationUseCase.Request(ipStr)

                val result = useCase(request)

                result.isSuccess shouldBe true
                val response = result.getOrNull()!!
                response.rdapInformation.ipAddress shouldBe ipAddress
                response.rdapInformation.handle shouldBe handle
                response.rdapInformation.name shouldBe name
                response.rdapInformation.country?.value shouldBe countryStr

                coVerify(exactly = 1) { mockRepository(ipAddress) }
            }
        }

        context("GetRdapInformationUseCase with invalid IP address") {
            data class InvalidIpCase(
                val invalidIp: String,
                val description: String,
            )

            withData(
                InvalidIpCase("", "empty string"),
                InvalidIpCase("   ", "whitespace"),
                InvalidIpCase("invalid-ip", "invalid format"),
                InvalidIpCase("256.1.1.1", "octet out of range"),
                InvalidIpCase("192.168.1.1.1", "too many octets"),
            ) { (invalidIp, _) ->
                val mockRepository = mockk<RdapRepository>()
                val useCase = getRdapInformationUseCase(mockRepository)
                val request = GetRdapInformationUseCase.Request(invalidIp)

                val result = useCase(request)

                result.isFailure shouldBe true
                result.exceptionOrNull() shouldNotBe null

                // Repository should not be called when IP validation fails
                coVerify(exactly = 0) { mockRepository(any()) }
            }
        }

        context("GetRdapInformationUseCase with repository errors") {
            test("should propagate repository errors") {
                val mockRepository = mockk<RdapRepository>()
                val ipAddress = IpAddress.of("192.0.2.1").getOrThrow()
                val repositoryError = RuntimeException("RDAP server unavailable")

                coEvery { mockRepository(ipAddress) } returns Result.failure(repositoryError)

                val useCase = getRdapInformationUseCase(mockRepository)
                val request = GetRdapInformationUseCase.Request("192.0.2.1")

                val result = useCase(request)

                result.isFailure shouldBe true
                result.exceptionOrNull() shouldBe repositoryError

                coVerify(exactly = 1) { mockRepository(ipAddress) }
            }

            test("should handle repository returning minimal data") {
                val mockRepository = mockk<RdapRepository>()
                val ipAddress = IpAddress.of("10.0.0.1").getOrThrow()
                val minimalRdapInfo =
                    RdapInformation(
                        ipAddress = ipAddress,
                        handle = null,
                        name = null,
                        country = null,
                        registeredAt = null,
                        rawData = emptyMap(),
                    )

                coEvery { mockRepository(ipAddress) } returns Result.success(minimalRdapInfo)

                val useCase = getRdapInformationUseCase(mockRepository)
                val request = GetRdapInformationUseCase.Request("10.0.0.1")

                val result = useCase(request)

                result.isSuccess shouldBe true
                val response = result.getOrNull()!!
                response.rdapInformation.handle shouldBe null
                response.rdapInformation.name shouldBe null
                response.rdapInformation.country shouldBe null
            }
        }
    })
