package io.github.seijikohara.devtools.domain.htmlreference.repository

import io.github.seijikohara.devtools.domain.common.model.PaginatedResult
import io.github.seijikohara.devtools.domain.common.model.Pagination
import io.github.seijikohara.devtools.domain.htmlreference.model.HtmlEntity

/**
 * Repository interface for accessing HTML entity data.
 *
 * @see HtmlEntity
 * @see PaginatedResult
 * @see Pagination
 */
fun interface HtmlEntityRepository {
    /**
     * Searches for HTML entities by name using case-insensitive partial matching.
     *
     * Returns an empty list if no entities match the search criteria.
     *
     * @param name The search keyword for partial matching against entity names
     * @param pagination The pagination parameters
     * @return [PaginatedResult] containing matching [HtmlEntity] instances
     */
    suspend fun searchByName(
        name: String,
        pagination: Pagination,
    ): PaginatedResult<HtmlEntity>
}
