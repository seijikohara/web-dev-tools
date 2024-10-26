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
    @Id @Column var id: Long?,
    @Column var name: String,
    @Column var code: Long,
    @Column var code2: Long?,
    @Column var standard: String?,
    @Column var dtd: String?,
    @Column var description: String?,
)

@Repository
interface HtmlEntityRepository : ReactiveCrudRepository<HtmlEntity, Long> {
    fun countByNameContaining(name: String): Mono<Long>

    fun findByNameContaining(
        name: String,
        pageable: Pageable,
    ): Flux<HtmlEntity>
}
