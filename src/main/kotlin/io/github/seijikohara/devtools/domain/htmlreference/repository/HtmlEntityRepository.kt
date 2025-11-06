package io.github.seijikohara.devtools.domain.htmlreference.repository

import io.github.seijikohara.devtools.domain.common.model.PaginatedResult
import io.github.seijikohara.devtools.domain.common.model.Pagination
import io.github.seijikohara.devtools.domain.htmlreference.model.HtmlEntity

/**
 * Repository port for HTML entities.
 *
 * Defines the contract for retrieving HTML entity information.
 * This is a port in Clean Architecture, implemented by infrastructure adapters.
 *
 * Functional interface (SAM) allows for concise lambda implementations.
 */
fun interface HtmlEntityRepository {
    /**
     * Search for HTML entities by name.
     *
     * @param name The search keyword (partial match)
     * @param pagination The pagination parameters
     * @return Paginated result containing matching HTML entities
     */
    suspend fun searchByName(
        name: String,
        pagination: Pagination,
    ): PaginatedResult<HtmlEntity>
}
