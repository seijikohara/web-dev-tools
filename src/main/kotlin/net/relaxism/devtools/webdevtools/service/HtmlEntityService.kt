package net.relaxism.devtools.webdevtools.service

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import net.relaxism.devtools.webdevtools.repository.HtmlEntity
import net.relaxism.devtools.webdevtools.repository.HtmlEntityRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class HtmlEntityService(
    private val htmlEntityRepository: HtmlEntityRepository,
) {
    suspend fun findAll(): Flow<HtmlEntity> = htmlEntityRepository.findAll().asFlow()

    suspend fun findByNameContaining(
        name: String,
        pageable: Pageable,
    ): Page<HtmlEntity> =
        coroutineScope {
            val countDeferred = async { htmlEntityRepository.countByNameContaining(name).awaitSingle() }
            val entitiesDeferred = async { htmlEntityRepository.findByNameContaining(name, pageable).asFlow().toList() }
            val count = countDeferred.await()
            val entities = entitiesDeferred.await()
            PageImpl(entities, pageable, count)
        }
}
