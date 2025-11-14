package io.github.seijikohara.devtools.domain.htmlreference.model

/**
 * Represents the numeric code of an HTML entity.
 *
 * Wraps a validated Unicode code point for HTML character entities.
 *
 * @property value The numeric code value, must be non-negative
 * @see toHexString
 * @see toDecimalString
 */
@JvmInline
value class EntityCode private constructor(
    val value: Long,
) {
    companion object {
        /**
         * Creates an [EntityCode] from a numeric value with validation.
         *
         * @param value The numeric code, must be non-negative
         * @return [Result.success] containing the [EntityCode] if valid,
         *         or [Result.failure] with [IllegalArgumentException] if negative
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
     * Returns a string in the format `&#xHHH;` where HHH is the hexadecimal representation.
     *
     * @return The hexadecimal HTML entity string
     */
    fun toHexString(): String = "&#x${value.toString(16)};"

    /**
     * Converts this entity code to decimal HTML entity format.
     *
     * Returns a string in the format `&#DDD;` where DDD is the decimal representation.
     *
     * @return The decimal HTML entity string
     */
    fun toDecimalString(): String = "&#$value;"

    /**
     * Returns the numeric value as a string.
     *
     * Note: Use [toHexString] or [toDecimalString] for HTML entity format.
     *
     * @return The numeric value as a string
     */
    override fun toString(): String = value.toString()
}
