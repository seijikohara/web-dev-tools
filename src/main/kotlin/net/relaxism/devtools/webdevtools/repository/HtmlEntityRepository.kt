package net.relaxism.devtools.webdevtools.repository

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Pageable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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
interface HtmlEntityRepository : ReactiveCrudRepository<HtmlEntity, Long> {
    fun countByNameContaining(name: String): Mono<Long>

    fun findByNameContaining(
        name: String,
        pageable: Pageable,
    ): Flux<HtmlEntity>
}
