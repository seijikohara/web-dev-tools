package io.github.seijikohara.devtools.domain.common.model

/**
 * Represents a page size for pagination.
 *
 * This is a value object that ensures the page size is within valid bounds (1 to MAX_SIZE).
 *
 * @property value The page size value
 */
@JvmInline
value class PageSize private constructor(
    val value: Int,
) {
    companion object {
        private const val MIN_SIZE = 1
        private const val MAX_SIZE = 1000

        /**
         * Creates a PageSize instance with validation.
         *
         * @param value The page size value to validate
         * @return Result containing PageSize if valid, or an error if invalid
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
