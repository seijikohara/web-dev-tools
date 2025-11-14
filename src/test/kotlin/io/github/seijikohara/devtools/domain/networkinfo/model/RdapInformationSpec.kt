package io.github.seijikohara.devtools.domain.networkinfo.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Duration
import java.time.Instant

class RdapInformationSpec :
    FunSpec({

        context("RdapInformation.isStale() with null registeredAt") {
            test("should return false when registeredAt is null") {
                val ipAddress = IpAddress.of("192.0.2.1").getOrThrow()
                val rdapInfo =
                    RdapInformation(
                        ipAddress = ipAddress,
                        handle = "HANDLE123",
                        name = "Example Org",
                        country = CountryCode.of("US").getOrNull(),
                        registeredAt = null,
                    )

                rdapInfo.isStale() shouldBe false
            }
        }

        context("RdapInformation.isStale() with recent registeredAt") {
            test("should return false when registered within default threshold (365 days)") {
                val ipAddress = IpAddress.of("192.0.2.1").getOrThrow()
                val recentDate = Instant.now().minus(Duration.ofDays(180)) // 180 days ago

                val rdapInfo =
                    RdapInformation(
                        ipAddress = ipAddress,
                        handle = "HANDLE123",
                        name = "Example Org",
                        country = CountryCode.of("US").getOrNull(),
                        registeredAt = recentDate,
                    )

                rdapInfo.isStale() shouldBe false
            }

            test("should return false when registered just before threshold") {
                val ipAddress = IpAddress.of("192.0.2.1").getOrThrow()
                val almostStaleDate = Instant.now().minus(Duration.ofDays(364)) // 364 days ago

                val rdapInfo =
                    RdapInformation(
                        ipAddress = ipAddress,
                        handle = "HANDLE123",
                        name = "Example Org",
                        country = CountryCode.of("US").getOrNull(),
                        registeredAt = almostStaleDate,
                    )

                rdapInfo.isStale() shouldBe false
            }
        }

        context("RdapInformation.isStale() with old registeredAt") {
            test("should return true when registered beyond default threshold (365 days)") {
                val ipAddress = IpAddress.of("192.0.2.1").getOrThrow()
                val oldDate = Instant.now().minus(Duration.ofDays(400)) // 400 days ago

                val rdapInfo =
                    RdapInformation(
                        ipAddress = ipAddress,
                        handle = "HANDLE123",
                        name = "Example Org",
                        country = CountryCode.of("US").getOrNull(),
                        registeredAt = oldDate,
                    )

                rdapInfo.isStale() shouldBe true
            }

            test("should return true when registered many years ago") {
                val ipAddress = IpAddress.of("192.0.2.1").getOrThrow()
                val veryOldDate = Instant.now().minus(Duration.ofDays(1000)) // ~3 years ago

                val rdapInfo =
                    RdapInformation(
                        ipAddress = ipAddress,
                        handle = "HANDLE123",
                        name = "Example Org",
                        country = CountryCode.of("US").getOrNull(),
                        registeredAt = veryOldDate,
                    )

                rdapInfo.isStale() shouldBe true
            }
        }

        context("RdapInformation.isStale() with custom threshold") {
            test("should use custom threshold for staleness check") {
                val ipAddress = IpAddress.of("192.0.2.1").getOrThrow()
                val date = Instant.now().minus(Duration.ofDays(100))

                val rdapInfo =
                    RdapInformation(
                        ipAddress = ipAddress,
                        handle = "HANDLE123",
                        name = "Example Org",
                        country = CountryCode.of("US").getOrNull(),
                        registeredAt = date,
                    )

                // With 50 days threshold, 100 days old should be stale
                rdapInfo.isStale(threshold = Duration.ofDays(50)) shouldBe true

                // With 200 days threshold, 100 days old should not be stale
                rdapInfo.isStale(threshold = Duration.ofDays(200)) shouldBe false
            }
        }

        context("RdapInformation data class properties") {
            test("should correctly store all properties") {
                val ipAddress = IpAddress.of("203.0.113.1").getOrThrow()
                val countryCode = CountryCode.of("JP").getOrNull()
                val registeredAt = Instant.parse("2020-01-01T00:00:00Z")

                val rdapInfo =
                    RdapInformation(
                        ipAddress = ipAddress,
                        handle = "JPNIC-123",
                        name = "Example Japan",
                        country = countryCode,
                        registeredAt = registeredAt,
                    )

                rdapInfo.ipAddress shouldBe ipAddress
                rdapInfo.handle shouldBe "JPNIC-123"
                rdapInfo.name shouldBe "Example Japan"
                rdapInfo.country shouldBe countryCode
                rdapInfo.registeredAt shouldBe registeredAt
            }

            test("should handle null optional properties") {
                val ipAddress = IpAddress.of("198.51.100.1").getOrThrow()

                val rdapInfo =
                    RdapInformation(
                        ipAddress = ipAddress,
                        handle = null,
                        name = null,
                        country = null,
                        registeredAt = null,
                    )

                rdapInfo.handle shouldBe null
                rdapInfo.name shouldBe null
                rdapInfo.country shouldBe null
                rdapInfo.registeredAt shouldBe null
            }
        }
    })
