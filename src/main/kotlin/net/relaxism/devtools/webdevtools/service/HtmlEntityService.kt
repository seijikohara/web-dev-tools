package net.relaxism.devtools.webdevtools.service

import net.relaxism.devtools.webdevtools.repository.HtmlEntity
import net.relaxism.devtools.webdevtools.repository.HtmlEntityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class HtmlEntityService(@Autowired private val htmlEntityRepository: HtmlEntityRepository) {

    fun findAll(): Flux<HtmlEntity> = htmlEntityRepository.findAll()

    fun findByNameContaining(name: String, pageable: Pageable): Mono<Page<HtmlEntity>> {
        return Mono.zip(
            htmlEntityRepository.findByNameContaining(name, pageable).collectList(),
            htmlEntityRepository.countByNameContaining(name)
        ).map { PageImpl(it.t1, pageable, it.t2) }
    }

}
