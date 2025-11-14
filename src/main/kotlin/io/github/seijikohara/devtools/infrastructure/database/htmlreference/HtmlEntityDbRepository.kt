package io.github.seijikohara.devtools.infrastructure.database.htmlreference

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

/**
 * Database repository for HTML entities.
 */
@Repository
interface HtmlEntityDbRepository : CoroutineCrudRepository<HtmlEntityDbEntity, Long> {
    /**
     * Counts HTML entities by name containing keyword.
     *
     * @param name Search keyword
     * @return Total count of matching entities
     */
    suspend fun countByNameContaining(name: String): Long

    /**
     * Finds HTML entities by name containing keyword with pagination.
     *
     * @param name Search keyword
     * @param pageable Pagination and sorting information
     * @return [Flow] of matching entities
     */
    fun findByNameContaining(
        name: String,
        pageable: Pageable,
    ): Flow<HtmlEntityDbEntity>
}
