package io.github.seijikohara.devtools.application.usecase

import io.github.seijikohara.devtools.domain.common.model.Pagination
import io.github.seijikohara.devtools.domain.htmlreference.model.HtmlEntity
import io.github.seijikohara.devtools.domain.htmlreference.repository.HtmlEntityRepository

/**
 * Use case for searching HTML entities.
 *
 * Provides business logic for HTML entity search with pagination.
 */
fun interface SearchHtmlEntitiesUseCase {
    /**
     * Executes the use case.
     *
     * @param request The use case request containing search parameters
     * @return Result containing Response if successful, or a failure with an exception
     */
    suspend operator fun invoke(request: Request): Result<Response>

    /**
     * Request for searching HTML entities.
     *
     * @param name Search keyword (partial match on entity name)
     * @param page Page number (0-indexed)
     * @param size Number of items per page
     */
    data class Request(
        val name: String,
        val page: Int = 0,
        val size: Int = 50,
    )

    /**
     * Response containing search results with pagination.
     *
     * @param entities List of matching HTML entities
     * @param totalCount Total number of matching entities
     * @param page Current page number
     * @param size Number of items per page
     */
    data class Response(
        val entities: List<HtmlEntity>,
        val totalCount: Long,
        val page: Int,
        val size: Int,
    )
}

/**
 * Factory function for creating SearchHtmlEntitiesUseCase.
 *
 * @param repository HTML entity repository
 * @return Use case instance
 */
fun searchHtmlEntitiesUseCase(repository: HtmlEntityRepository): SearchHtmlEntitiesUseCase =
    SearchHtmlEntitiesUseCase { request ->
        Pagination
            .of(request.page * request.size, request.size)
            .mapCatching { pagination ->
                repository.searchByName(
                    name = request.name,
                    pagination = pagination,
                )
            }.map { paginatedResult ->
                SearchHtmlEntitiesUseCase.Response(
                    entities = paginatedResult.items,
                    totalCount = paginatedResult.totalCount,
                    page = request.page,
                    size = request.size,
                )
            }
    }
