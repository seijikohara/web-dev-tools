package io.github.seijikohara.devtools.domain.common.model

/**
 * Represents pagination information.
 *
 * This value object encapsulates offset-based pagination parameters and provides
 * convenient methods for calculating page numbers.
 *
 * @property offset The zero-based offset (number of items to skip)
 * @property pageSize The number of items per page
 */
data class Pagination(
    val offset: Int,
    val pageSize: PageSize,
) {
    init {
        require(offset >= 0) { "Offset must be non-negative, but got $offset" }
    }

    /**
     * Calculates the current page number (zero-based).
     */
    val pageNumber: Int
        get() = offset / pageSize.value

    /**
     * Calculates the next page's offset.
     */
    val nextOffset: Int
        get() = offset + pageSize.value

    companion object {
        /**
         * Creates a Pagination instance with validation.
         *
         * @param offset The zero-based offset
         * @param pageSize The page size value
         * @return Result containing Pagination if valid, or an error if invalid
         */
        fun of(
            offset: Int,
            pageSize: Int,
        ): Result<Pagination> =
            PageSize.of(pageSize).mapCatching { validPageSize ->
                Pagination(offset, validPageSize)
            }
    }
}
