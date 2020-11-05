package net.relaxism.devtools.webdevtools.service

import net.relaxism.devtools.webdevtools.repository.HtmlEntity
import net.relaxism.devtools.webdevtools.repository.HtmlEntityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class HtmlEntityService(@Autowired private val htmlEntityRepository: HtmlEntityRepository) {

    fun findAll(): Flux<HtmlEntity> = htmlEntityRepository.findAll()

}
