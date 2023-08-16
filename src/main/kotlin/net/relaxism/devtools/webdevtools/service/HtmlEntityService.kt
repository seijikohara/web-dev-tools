package net.relaxism.devtools.webdevtools.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import net.relaxism.devtools.webdevtools.repository.HtmlEntity
import net.relaxism.devtools.webdevtools.repository.HtmlEntityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class HtmlEntityService(
    @Autowired private val htmlEntityRepository: HtmlEntityRepository
) {

    suspend fun findAll(): Flow<HtmlEntity> = htmlEntityRepository.findAll().asFlow()

    suspend fun findByNameContaining(name: String, pageable: Pageable): Page<HtmlEntity> {
        return Mono.zip(
            htmlEntityRepository.findByNameContaining(name, pageable).collectList(),
            htmlEntityRepository.countByNameContaining(name)
        ).map { PageImpl(it.t1, pageable, it.t2) }
            .awaitSingle()
    }
}
