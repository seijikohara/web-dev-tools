package io.github.seijikohara.devtools.domain.common.model

/**
 * Represents the number of items per page in paginated results.
 *
 * Wraps a validated page size value with defined minimum and maximum bounds.
 *
 * @property value The validated page size value
 * @see of
 */
@JvmInline
value class PageSize private constructor(
    val value: Int,
) {
    companion object {
        /** The minimum allowed page size. */
        private const val MIN_SIZE = 1

        /** The maximum allowed page size. */
        private const val MAX_SIZE = 1000

        /**
         * Creates a [PageSize] instance with validation.
         *
         * @param value The page size value to validate
         * @return [Result.success] containing the [PageSize] if valid,
         *         or [Result.failure] with [IllegalArgumentException] if out of valid range
         */
        fun of(value: Int): Result<PageSize> =
            runCatching {
                require(value in MIN_SIZE..MAX_SIZE) {
                    "Page size must be between $MIN_SIZE and $MAX_SIZE, but got $value"
                }
                PageSize(value)
            }
    }
}
