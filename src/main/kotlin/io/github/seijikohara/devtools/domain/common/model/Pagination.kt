package io.github.seijikohara.devtools.domain.common.model

/**
 * Represents pagination parameters for querying paginated data.
 *
 * Encapsulates zero-based offset and page size for fetching specific pages of results.
 *
 * @property offset The zero-based offset, must be non-negative
 * @property pageSize The number of items per page
 * @see PageSize
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
     *
     * Computed by integer division of offset by page size.
     */
    val pageNumber: Int
        get() = offset / pageSize.value

    /**
     * Calculates the offset for the next page.
     */
    val nextOffset: Int
        get() = offset + pageSize.value

    companion object {
        /**
         * Creates a [Pagination] instance with validation.
         *
         * Validates both offset and page size before creating the instance.
         *
         * @param offset The zero-based offset
         * @param pageSize The page size value
         * @return [Result.success] containing the [Pagination] if valid,
         *         or [Result.failure] with [IllegalArgumentException] if invalid
         * @see PageSize.of
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
