package io.github.seijikohara.devtools.domain.htmlreference.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class EntityCodeSpec :
    FunSpec({

        context("EntityCode.of() with valid values") {
            data class ValidValueCase(
                val value: Long,
                val description: String,
            )

            withData(
                ValidValueCase(0L, "zero"),
                ValidValueCase(1L, "one"),
                ValidValueCase(38L, "ampersand code"),
                ValidValueCase(169L, "copyright code"),
                ValidValueCase(8364L, "euro sign code"),
                ValidValueCase(Long.MAX_VALUE, "maximum long value"),
            ) { (value, _) ->
                val result = EntityCode.of(value)
                result.isSuccess shouldBe true
                result.getOrNull()?.value shouldBe value
            }
        }

        context("EntityCode.of() with invalid values") {
            data class InvalidValueCase(
                val value: Long,
                val description: String,
            )

            withData(
                InvalidValueCase(-1L, "minus one"),
                InvalidValueCase(-10L, "minus ten"),
                InvalidValueCase(-1000L, "large negative"),
                InvalidValueCase(Long.MIN_VALUE, "minimum long value"),
            ) { (value, _) ->
                val result = EntityCode.of(value)
                result.isFailure shouldBe true
                result.exceptionOrNull()?.message shouldContain "must be non-negative"
                result.exceptionOrNull()?.message shouldContain value.toString()
            }
        }

        context("EntityCode.toHexString()") {
            data class HexStringCase(
                val value: Long,
                val expected: String,
                val description: String,
            )

            withData(
                HexStringCase(38L, "&#x26;", "ampersand"),
                HexStringCase(169L, "&#xa9;", "copyright"),
                HexStringCase(60L, "&#x3c;", "less than"),
                HexStringCase(62L, "&#x3e;", "greater than"),
                HexStringCase(34L, "&#x22;", "quotation mark"),
                HexStringCase(0L, "&#x0;", "zero"),
                HexStringCase(255L, "&#xff;", "max single byte"),
            ) { (value, expected, _) ->
                val entityCode = EntityCode.of(value).getOrThrow()
                entityCode.toHexString() shouldBe expected
            }
        }

        context("EntityCode.toDecimalString()") {
            data class DecimalStringCase(
                val value: Long,
                val expected: String,
                val description: String,
            )

            withData(
                DecimalStringCase(38L, "&#38;", "ampersand"),
                DecimalStringCase(169L, "&#169;", "copyright"),
                DecimalStringCase(60L, "&#60;", "less than"),
                DecimalStringCase(62L, "&#62;", "greater than"),
                DecimalStringCase(34L, "&#34;", "quotation mark"),
                DecimalStringCase(0L, "&#0;", "zero"),
                DecimalStringCase(8364L, "&#8364;", "euro sign"),
            ) { (value, expected, _) ->
                val entityCode = EntityCode.of(value).getOrThrow()
                entityCode.toDecimalString() shouldBe expected
            }
        }

        context("EntityCode.toString()") {
            data class ToStringCase(
                val value: Long,
                val expected: String,
            )

            withData(
                ToStringCase(0L, "0"),
                ToStringCase(38L, "38"),
                ToStringCase(169L, "169"),
                ToStringCase(1234L, "1234"),
            ) { (value, expected) ->
                val entityCode = EntityCode.of(value).getOrThrow()
                entityCode.toString() shouldBe expected
            }
        }
    })
