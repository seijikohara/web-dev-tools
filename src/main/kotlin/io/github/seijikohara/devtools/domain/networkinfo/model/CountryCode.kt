package io.github.seijikohara.devtools.domain.networkinfo.model

/**
 * ISO 3166-1 alpha-2 country code value object.
 *
 * Represents a two-letter country code in uppercase format.
 * Examples: "US", "JP", "GB"
 *
 * @property value The string representation of the country code
 */
@JvmInline
value class CountryCode private constructor(
    val value: String,
) {
    companion object {
        /**
         * Creates a CountryCode from a string value.
         *
         * @param value The string representation of a country code (must be 2 uppercase letters)
         * @return Result containing the CountryCode if valid, or a failure with an exception
         */
        fun of(value: String): Result<CountryCode> =
            runCatching {
                require(value.length == 2) { "Country code must be exactly 2 characters, but was: ${value.length}" }
                require(value.all { it.isUpperCase() && it.isLetter() }) {
                    "Country code must consist of uppercase letters only: $value"
                }
                CountryCode(value)
            }
    }

    override fun toString(): String = value
}
