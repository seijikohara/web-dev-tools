package io.github.seijikohara.devtools.domain.htmlreference.model

/**
 * HTML entity code value object.
 *
 * Represents a numeric code for HTML entities (e.g., 38 for &amp;, 169 for &copy;).
 *
 * @property value The numeric code value (non-negative)
 */
@JvmInline
value class EntityCode private constructor(
    val value: Long,
) {
    companion object {
        /**
         * Creates an EntityCode from a numeric value.
         *
         * @param value The numeric code (must be non-negative)
         * @return Result containing the EntityCode if valid, or a failure with an exception
         */
        fun of(value: Long): Result<EntityCode> =
            runCatching {
                require(value >= 0) { "Entity code must be non-negative, but was: $value" }
                EntityCode(value)
            }
    }

    /**
     * Converts this entity code to hexadecimal HTML entity format.
     *
     * Example: EntityCode(38) -> "&#x26;"
     *
     * @return The hexadecimal HTML entity string
     */
    fun toHexString(): String = "&#x${value.toString(16)};"

    /**
     * Converts this entity code to decimal HTML entity format.
     *
     * Example: EntityCode(38) -> "&#38;"
     *
     * @return The decimal HTML entity string
     */
    fun toDecimalString(): String = "&#$value;"

    /**
     * Returns the string representation of the entity code value.
     *
     * @return The numeric value as a string
     */
    override fun toString(): String = value.toString()
}
