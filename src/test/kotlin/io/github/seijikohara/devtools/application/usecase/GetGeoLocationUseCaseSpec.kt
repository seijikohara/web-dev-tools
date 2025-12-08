package io.github.seijikohara.devtools.application.usecase

import io.github.seijikohara.devtools.domain.networkinfo.model.GeoLocation
import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.repository.GeoIpRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class GetGeoLocationUseCaseSpec :
    FunSpec({

        context("GetGeoLocationUseCase execution") {
            data class GeoLocationTestCase(
                val ipStr: String,
                val countryCode: String,
                val city: String,
                val lat: Double,
                val lng: Double,
            )

            withData(
                GeoLocationTestCase("192.168.1.1", "US", "New York", 40.7128, -74.0060),
                GeoLocationTestCase("8.8.8.8", "US", "Mountain View", 37.386, -122.0838),
                GeoLocationTestCase("2001:db8::1", "JP", "Tokyo", 35.6762, 139.6503),
            ) { (ipStr, countryCode, city, lat, lng) ->
                val mockRepository = mockk<GeoIpRepository>()
                val ipAddress = IpAddress.of(ipStr).getOrThrow()
                val expectedGeoLocation =
                    GeoLocation(
                        ip = ipStr,
                        countryCode = countryCode,
                        city = city,
                        latitude = lat,
                        longitude = lng,
                    )

                coEvery { mockRepository(ipAddress) } returns Result.success(expectedGeoLocation)

                val useCase = getGeoLocationUseCase(mockRepository)
                val request = GetGeoLocationUseCase.Request(ipStr)

                val result = useCase(request)

                result.isSuccess shouldBe true
                val response = result.getOrNull()!!
                response.geoLocation.ip shouldBe ipStr
                response.geoLocation.countryCode shouldBe countryCode
                response.geoLocation.city shouldBe city
                response.geoLocation.latitude shouldBe lat
                response.geoLocation.longitude shouldBe lng

                coVerify(exactly = 1) { mockRepository(ipAddress) }
            }
        }

        context("GetGeoLocationUseCase with invalid IP address") {
            data class InvalidIpCase(
                val invalidIp: String,
                val description: String,
            )

            withData(
                InvalidIpCase("", "empty string"),
                InvalidIpCase("   ", "whitespace"),
                InvalidIpCase("999.999.999.999", "invalid format"),
                InvalidIpCase("256.1.1.1", "octet out of range"),
                InvalidIpCase("192.168.1.1.1", "too many octets"),
            ) { (invalidIp, _) ->
                val mockRepository = mockk<GeoIpRepository>()
                val useCase = getGeoLocationUseCase(mockRepository)
                val request = GetGeoLocationUseCase.Request(invalidIp)

                val result = useCase(request)

                result.isFailure shouldBe true
                result.exceptionOrNull() shouldNotBe null

                // Repository should not be called when IP validation fails
                coVerify(exactly = 0) { mockRepository(any()) }
            }
        }

        context("GetGeoLocationUseCase with repository errors") {
            test("should propagate repository errors") {
                val mockRepository = mockk<GeoIpRepository>()
                val ipAddress = IpAddress.of("192.0.2.1").getOrThrow()
                val repositoryError = RuntimeException("Network error")

                coEvery { mockRepository(ipAddress) } returns Result.failure(repositoryError)

                val useCase = getGeoLocationUseCase(mockRepository)
                val request = GetGeoLocationUseCase.Request("192.0.2.1")

                val result = useCase(request)

                result.isFailure shouldBe true
                result.exceptionOrNull() shouldBe repositoryError

                coVerify(exactly = 1) { mockRepository(ipAddress) }
            }

            test("should handle repository returning incomplete data") {
                val mockRepository = mockk<GeoIpRepository>()
                val ipAddress = IpAddress.of("10.0.0.1").getOrThrow()
                val minimalGeoLocation =
                    GeoLocation(
                        ip = "10.0.0.1",
                        countryCode = null,
                        city = null,
                        latitude = null,
                        longitude = null,
                    )

                coEvery { mockRepository(ipAddress) } returns Result.success(minimalGeoLocation)

                val useCase = getGeoLocationUseCase(mockRepository)
                val request = GetGeoLocationUseCase.Request("10.0.0.1")

                val result = useCase(request)

                result.isSuccess shouldBe true
                val response = result.getOrNull()!!
                response.geoLocation.countryCode shouldBe null
                response.geoLocation.city shouldBe null
            }
        }
    })
