package com.github.seijikohara.devtools.webdevtools.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Pageable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Table
data class HtmlEntity(
    @Id @Column val id: Long? = null,
    @Column val name: String,
    @Column val code: Long,
    @Column val code2: Long? = null,
    @Column val standard: String? = null,
    @Column val dtd: String? = null,
    @Column val description: String? = null,
)

@Repository
interface HtmlEntityRepository : CoroutineCrudRepository<HtmlEntity, Long> {
    suspend fun countByNameContaining(name: String): Long

    fun findByNameContaining(
        name: String,
        pageable: Pageable,
    ): Flow<HtmlEntity>
}
