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
 * @property dbRepository Database repository for HTML entities
 */
class HtmlEntityRepositoryAdapter(
    private val dbRepository: HtmlEntityDbRepository,
) : HtmlEntityRepository {
    /**
     * Searches HTML entities by name with pagination.
     *
     * @param name Search keyword for entity name
     * @param pagination Pagination parameters
     * @return [PaginatedResult] containing [HtmlEntity] instances
     */
    override suspend fun searchByName(
        name: String,
        pagination: Pagination,
    ): PaginatedResult<HtmlEntity> =
        coroutineScope {
            Pair(
                async { dbRepository.countByNameContaining(name) },
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
                },
            ).let { (count, entities) ->
                PaginatedResult(
                    totalCount = count.await(),
                    items = entities.await(),
                    pagination = pagination,
                )
            }
        }
}
