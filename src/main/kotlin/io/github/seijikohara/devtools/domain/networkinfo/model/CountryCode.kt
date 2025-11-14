package io.github.seijikohara.devtools.domain.networkinfo.model

/**
 * Represents an ISO 3166-1 alpha-2 country code.
 *
 * Wraps a validated two-character uppercase country code string conforming to ISO 3166-1 alpha-2.
 *
 * @property value The two-character uppercase country code string
 */
@JvmInline
value class CountryCode private constructor(
    val value: String,
) {
    companion object {
        /**
         * Creates a [CountryCode] from a string value with validation.
         *
         * Validates that the input has exactly two uppercase letters.
         *
         * @param value The country code string
         * @return [Result.success] containing the [CountryCode] if valid,
         *         or [Result.failure] with [IllegalArgumentException] if invalid format
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

    /**
     * Returns the string representation of this country code.
     *
     * @return The two-character uppercase country code
     */
    override fun toString(): String = value
}
