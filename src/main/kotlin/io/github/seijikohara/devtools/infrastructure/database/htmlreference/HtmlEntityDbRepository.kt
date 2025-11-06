package io.github.seijikohara.devtools.infrastructure.database.htmlreference

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data R2DBC repository for HTML entities.
 *
 * Provides database access using Spring Data's reactive coroutine support.
 */
@Repository
interface HtmlEntityDbRepository : CoroutineCrudRepository<HtmlEntityDbEntity, Long> {
    /**
     * Count HTML entities by name containing keyword.
     *
     * @param name Search keyword (case-insensitive partial match)
     * @return Total count of matching entities
     */
    suspend fun countByNameContaining(name: String): Long

    /**
     * Find HTML entities by name containing keyword with pagination.
     *
     * @param name Search keyword (case-insensitive partial match)
     * @param pageable Pagination and sorting information
     * @return Flow of matching entities
     */
    fun findByNameContaining(
        name: String,
        pageable: Pageable,
    ): Flow<HtmlEntityDbEntity>
}
