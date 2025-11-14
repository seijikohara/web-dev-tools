package io.github.seijikohara.devtools.application.usecase

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class GetIpInfoUseCaseSpec :
    FunSpec({

        context("GetIpInfoUseCase execution") {
            test("should extract IP from X-Forwarded-For header with single IP") {
                val useCase = getIpInfoUseCase()
                val request =
                    GetIpInfoUseCase.Request(
                        xForwardedFor = "192.168.1.1",
                        xRealIp = null,
                        remoteAddress = null,
                        canonicalHostName = null,
                    )

                val response = useCase(request)

                response.ipAddress shouldBe "192.168.1.1"
                response.hostName shouldBe null
            }

            test("should extract first IP from X-Forwarded-For header with multiple IPs") {
                val useCase = getIpInfoUseCase()
                val request =
                    GetIpInfoUseCase.Request(
                        xForwardedFor = "192.168.1.1, 10.0.0.1, 172.16.0.1",
                        xRealIp = null,
                        remoteAddress = null,
                        canonicalHostName = null,
                    )

                val response = useCase(request)

                response.ipAddress shouldBe "192.168.1.1"
            }

            data class TrimTestCase(
                val description: String,
                val xForwardedFor: String?,
                val xRealIp: String?,
                val expectedIp: String,
            )

            withData(
                nameFn = { it.description },
                TrimTestCase(
                    description = "should trim whitespace from X-Forwarded-For single IP",
                    xForwardedFor = "  192.168.1.1  ",
                    xRealIp = null,
                    expectedIp = "192.168.1.1",
                ),
                TrimTestCase(
                    description = "should trim whitespace from X-Forwarded-For first IP in chain",
                    xForwardedFor = " 192.168.1.1 , 10.0.0.1",
                    xRealIp = null,
                    expectedIp = "192.168.1.1",
                ),
                TrimTestCase(
                    description = "should trim whitespace from X-Real-IP",
                    xForwardedFor = null,
                    xRealIp = "  192.168.1.2  ",
                    expectedIp = "192.168.1.2",
                ),
            ) { (_, xForwardedFor, xRealIp, expectedIp) ->
                val useCase = getIpInfoUseCase()
                val request =
                    GetIpInfoUseCase.Request(
                        xForwardedFor = xForwardedFor,
                        xRealIp = xRealIp,
                        remoteAddress = null,
                        canonicalHostName = null,
                    )

                val response = useCase(request)

                response.ipAddress shouldBe expectedIp
            }

            test("should extract IP from X-Real-IP header when X-Forwarded-For is not present") {
                val useCase = getIpInfoUseCase()
                val request =
                    GetIpInfoUseCase.Request(
                        xForwardedFor = null,
                        xRealIp = "192.168.1.2",
                        remoteAddress = null,
                        canonicalHostName = null,
                    )

                val response = useCase(request)

                response.ipAddress shouldBe "192.168.1.2"
            }

            test("should extract IP from remoteAddress when headers are not present") {
                val useCase = getIpInfoUseCase()
                val request =
                    GetIpInfoUseCase.Request(
                        xForwardedFor = null,
                        xRealIp = null,
                        remoteAddress = "192.168.1.3",
                        canonicalHostName = null,
                    )

                val response = useCase(request)

                response.ipAddress shouldBe "192.168.1.3"
            }

            data class PriorityTestCase(
                val description: String,
                val xForwardedFor: String?,
                val xRealIp: String?,
                val remoteAddress: String?,
                val expectedIp: String,
            )

            withData(
                nameFn = { it.description },
                PriorityTestCase(
                    description = "should prioritize X-Forwarded-For over X-Real-IP",
                    xForwardedFor = "192.168.1.1",
                    xRealIp = "192.168.1.2",
                    remoteAddress = null,
                    expectedIp = "192.168.1.1",
                ),
                PriorityTestCase(
                    description = "should prioritize X-Forwarded-For over remoteAddress",
                    xForwardedFor = "192.168.1.1",
                    xRealIp = null,
                    remoteAddress = "192.168.1.3",
                    expectedIp = "192.168.1.1",
                ),
                PriorityTestCase(
                    description = "should prioritize X-Real-IP over remoteAddress",
                    xForwardedFor = null,
                    xRealIp = "192.168.1.2",
                    remoteAddress = "192.168.1.3",
                    expectedIp = "192.168.1.2",
                ),
                PriorityTestCase(
                    description = "should prioritize X-Forwarded-For over all others",
                    xForwardedFor = "192.168.1.1",
                    xRealIp = "192.168.1.2",
                    remoteAddress = "192.168.1.3",
                    expectedIp = "192.168.1.1",
                ),
            ) { (_, xForwardedFor, xRealIp, remoteAddress, expectedIp) ->
                val useCase = getIpInfoUseCase()
                val request =
                    GetIpInfoUseCase.Request(
                        xForwardedFor = xForwardedFor,
                        xRealIp = xRealIp,
                        remoteAddress = remoteAddress,
                        canonicalHostName = null,
                    )

                val response = useCase(request)

                response.ipAddress shouldBe expectedIp
            }

            test("should return null IP address when all sources are null") {
                val useCase = getIpInfoUseCase()
                val request =
                    GetIpInfoUseCase.Request(
                        xForwardedFor = null,
                        xRealIp = null,
                        remoteAddress = null,
                        canonicalHostName = null,
                    )

                val response = useCase(request)

                response.ipAddress shouldBe null
                response.hostName shouldBe null
            }

            test("should include hostName when canonicalHostName is provided") {
                val useCase = getIpInfoUseCase()
                val request =
                    GetIpInfoUseCase.Request(
                        xForwardedFor = "192.168.1.1",
                        xRealIp = null,
                        remoteAddress = null,
                        canonicalHostName = "example.com",
                    )

                val response = useCase(request)

                response.ipAddress shouldBe "192.168.1.1"
                response.hostName shouldBe "example.com"
            }

            test("should handle IPv6 addresses in X-Forwarded-For") {
                val useCase = getIpInfoUseCase()
                val request =
                    GetIpInfoUseCase.Request(
                        xForwardedFor = "2001:db8::1",
                        xRealIp = null,
                        remoteAddress = null,
                        canonicalHostName = null,
                    )

                val response = useCase(request)

                response.ipAddress shouldBe "2001:db8::1"
            }

            test("should extract first IPv6 from X-Forwarded-For chain") {
                val useCase = getIpInfoUseCase()
                val request =
                    GetIpInfoUseCase.Request(
                        xForwardedFor = "2001:db8::1, 2001:db8::2",
                        xRealIp = null,
                        remoteAddress = null,
                        canonicalHostName = null,
                    )

                val response = useCase(request)

                response.ipAddress shouldBe "2001:db8::1"
            }
        }
    })
