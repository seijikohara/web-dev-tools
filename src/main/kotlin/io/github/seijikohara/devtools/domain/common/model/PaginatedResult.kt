package io.github.seijikohara.devtools.domain.common.model

/**
 * Represents a paginated result containing items and pagination metadata.
 *
 * Encapsulates items for the current page along with total count and pagination parameters.
 *
 * @param T The type of items in the result
 * @property totalCount The total number of items across all pages, must be non-negative
 * @property items The items for the current page
 * @property pagination The pagination parameters used for this query
 */
data class PaginatedResult<T>(
    val totalCount: Long,
    val items: List<T>,
    val pagination: Pagination,
) {
    init {
        require(totalCount >= 0) { "Total count must be non-negative, but got $totalCount" }
    }

    /**
     * Indicates whether there are more items beyond the current page.
     */
    val hasMore: Boolean
        get() = pagination.offset + items.size < totalCount

    /**
     * Calculates the total number of pages using ceiling division.
     *
     * Returns zero if [totalCount] is zero.
     */
    val totalPages: Long
        get() =
            if (totalCount == 0L) {
                0
            } else {
                (totalCount + pagination.pageSize.value - 1) / pagination.pageSize.value
            }

    /**
     * Transforms the items using the given transform function.
     *
     * Creates a new [PaginatedResult] with transformed items while preserving pagination metadata.
     *
     * @param R The type of items in the resulting paginated result
     * @param transform The function to apply to each item
     * @return A new [PaginatedResult] with transformed items
     */
    fun <R> map(transform: (T) -> R): PaginatedResult<R> =
        PaginatedResult(
            totalCount = totalCount,
            items = items.map(transform),
            pagination = pagination,
        )
}
