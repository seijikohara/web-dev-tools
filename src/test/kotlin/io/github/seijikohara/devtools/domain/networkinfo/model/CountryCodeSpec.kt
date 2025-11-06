package io.github.seijikohara.devtools.domain.networkinfo.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class CountryCodeSpec :
    FunSpec({

        context("CountryCode.of() with valid country codes") {
            data class ValidCountryCodeCase(
                val code: String,
                val description: String,
            )

            withData(
                ValidCountryCodeCase("US", "United States"),
                ValidCountryCodeCase("JP", "Japan"),
                ValidCountryCodeCase("GB", "Great Britain"),
                ValidCountryCodeCase("DE", "Germany"),
                ValidCountryCodeCase("FR", "France"),
                ValidCountryCodeCase("CA", "Canada"),
                ValidCountryCodeCase("AU", "Australia"),
            ) { (code, _) ->
                val result = CountryCode.of(code)
                result.isSuccess shouldBe true
                val countryCode = result.getOrNull()!!
                countryCode.value shouldBe code
                countryCode.toString() shouldBe code
            }
        }

        context("CountryCode.of() with invalid country codes") {
            data class InvalidCountryCodeCase(
                val code: String,
                val description: String,
            )

            withData(
                InvalidCountryCodeCase("", "empty string"),
                InvalidCountryCodeCase("   ", "whitespace only"),
                InvalidCountryCodeCase("U", "too short"),
                InvalidCountryCodeCase("USA", "too long"),
                InvalidCountryCodeCase("us", "lowercase"),
                InvalidCountryCodeCase("Us", "mixed case"),
                InvalidCountryCodeCase("U1", "contains number"),
                InvalidCountryCodeCase("U!", "contains special char"),
                InvalidCountryCodeCase("12", "all numbers"),
            ) { (code, _) ->
                val result = CountryCode.of(code)
                result.isFailure shouldBe true
            }
        }

        context("CountryCode.of() error messages") {
            test("should contain appropriate error message for length validation") {
                val result = CountryCode.of("USA")
                result.isFailure shouldBe true
                result.exceptionOrNull()?.message shouldContain "must be exactly 2 characters"
            }

            test("should contain appropriate error message for empty string") {
                val result = CountryCode.of("")
                result.isFailure shouldBe true
                result.exceptionOrNull()?.message shouldContain "must be exactly 2 characters"
            }

            test("should contain appropriate error message for lowercase") {
                val result = CountryCode.of("us")
                result.isFailure shouldBe true
                result.exceptionOrNull()?.message shouldContain "must consist of uppercase letters only"
            }

            test("should contain appropriate error message for special characters") {
                val result = CountryCode.of("U!")
                result.isFailure shouldBe true
                result.exceptionOrNull()?.message shouldContain "must consist of uppercase letters only"
            }
        }

        context("CountryCode.toString()") {
            withData(
                "US",
                "JP",
                "GB",
            ) { code ->
                val countryCode = CountryCode.of(code).getOrThrow()
                countryCode.toString() shouldBe code
            }
        }
    })
