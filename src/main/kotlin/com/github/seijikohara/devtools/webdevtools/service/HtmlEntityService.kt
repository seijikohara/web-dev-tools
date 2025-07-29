package com.github.seijikohara.devtools.webdevtools.service

import com.github.seijikohara.devtools.webdevtools.repository.HtmlEntity
import com.github.seijikohara.devtools.webdevtools.repository.HtmlEntityRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class HtmlEntityService(
    private val htmlEntityRepository: HtmlEntityRepository,
) {
    suspend fun findAll(): Flow<HtmlEntity> = htmlEntityRepository.findAll()

    suspend fun findByNameContaining(
        name: String,
        pageable: Pageable,
    ): Page<HtmlEntity> =
        coroutineScope {
            async { htmlEntityRepository.countByNameContaining(name) } to
                async { htmlEntityRepository.findByNameContaining(name, pageable).toList() }
        }.let { (countDeferred, entitiesDeferred) ->
            PageImpl(entitiesDeferred.await(), pageable, countDeferred.await())
        }
}
