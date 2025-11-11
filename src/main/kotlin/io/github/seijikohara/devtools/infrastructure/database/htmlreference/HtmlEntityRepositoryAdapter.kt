package io.github.seijikohara.devtools.infrastructure.database.htmlreference

import io.github.seijikohara.devtools.domain.common.extensions.sequence
import io.github.seijikohara.devtools.domain.common.model.PaginatedResult
import io.github.seijikohara.devtools.domain.common.model.Pagination
import io.github.seijikohara.devtools.domain.htmlreference.model.HtmlEntity
import io.github.seijikohara.devtools.domain.htmlreference.repository.HtmlEntityRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

/**
 * Adapter implementation for HTML entity repository.
 *
 * Bridges domain layer with database infrastructure using Spring Data R2DBC.
 * Uses coroutines for concurrent count and fetch operations to optimize performance.
 *
 * @property dbRepository Spring Data R2DBC repository for database access
 */
class HtmlEntityRepositoryAdapter(
    private val dbRepository: HtmlEntityDbRepository,
) : HtmlEntityRepository {
    /**
     * Searches HTML entities by name with pagination.
     *
     * Performs concurrent database operations for count and fetch to optimize performance.
     * Converts database entities to domain entities using expression chains.
     *
     * @param name Search keyword for partial matching on entity name
     * @param pagination Pagination parameters
     * @return Paginated result containing domain entities
     */
    override suspend fun searchByName(
        name: String,
        pagination: Pagination,
    ): PaginatedResult<HtmlEntity> =
        coroutineScope {
            val countDeferred = async { dbRepository.countByNameContaining(name) }
            val entitiesDeferred =
                async {
                    dbRepository
                        .findByNameContaining(
                            name,
                            PageRequest.of(
                                pagination.pageNumber,
                                pagination.pageSize.value,
                                Sort.by(Sort.Order.asc("id")),
                            ),
                        ).toList()
                        .map { it.toDomain() }
                        .sequence()
                        .getOrThrow()
                }

            PaginatedResult(
                totalCount = countDeferred.await(),
                items = entitiesDeferred.await(),
                pagination = pagination,
            )
        }
}
