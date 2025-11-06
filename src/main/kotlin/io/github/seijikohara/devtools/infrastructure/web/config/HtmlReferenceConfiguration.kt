package io.github.seijikohara.devtools.infrastructure.web.config

import io.github.seijikohara.devtools.application.usecase.SearchHtmlEntitiesUseCase
import io.github.seijikohara.devtools.config.ApplicationProperties
import io.github.seijikohara.devtools.domain.common.extensions.flatMap
import io.github.seijikohara.devtools.domain.common.extensions.sequence
import io.github.seijikohara.devtools.domain.common.model.PaginatedResult
import io.github.seijikohara.devtools.domain.htmlreference.model.EntityCode
import io.github.seijikohara.devtools.domain.htmlreference.model.HtmlEntity
import io.github.seijikohara.devtools.domain.htmlreference.repository.HtmlEntityRepository
import io.github.seijikohara.devtools.infrastructure.database.htmlreference.HtmlEntityDbEntity
import io.github.seijikohara.devtools.infrastructure.database.htmlreference.HtmlEntityDbRepository
import io.github.seijikohara.devtools.infrastructure.web.handler.handleSearchHtmlEntities
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import io.github.seijikohara.devtools.application.usecase.searchHtmlEntitiesUseCase as createSearchHtmlEntitiesUseCase

/**
 * Configuration for HTML Reference bounded context.
 *
 * This configuration defines the beans and routing for HTML entity search features
 * following Clean Architecture principles.
 */
@Configuration
class HtmlReferenceConfiguration(
    private val applicationProperties: ApplicationProperties,
) {
    /**
     * Repository implementation for HTML entities.
     *
     * Functional implementation that bridges the domain layer with database infrastructure.
     * Uses coroutines for concurrent count and fetch operations to optimize performance.
     */
    @Bean
    fun htmlEntityRepository(dbRepository: HtmlEntityDbRepository): HtmlEntityRepository =
        HtmlEntityRepository { name, pagination ->
            coroutineScope {
                val countDeferred = async { dbRepository.countByNameContaining(name) }
                val entitiesDeferred =
                    async {
                        dbRepository
                            .findByNameContaining(
                                name,
                                PageRequest.of(
                                    pagination.pageNumber,
                                    pagination.pageSize.value,
                                    Sort.by(Sort.Order.asc("id")),
                                ),
                            ).toList()
                            .map { it.toDomain() }
                            .sequence()
                            .getOrThrow()
                    }

                PaginatedResult(
                    totalCount = countDeferred.await(),
                    items = entitiesDeferred.await(),
                    pagination = pagination,
                )
            }
        }

    /**
     * Use case for searching HTML entities.
     *
     * Factory function pattern allows for composition without exposing implementation details.
     */
    @Bean
    fun searchHtmlEntitiesUseCase(htmlEntityRepository: HtmlEntityRepository): SearchHtmlEntitiesUseCase =
        createSearchHtmlEntitiesUseCase(htmlEntityRepository)

    /**
     * Router for HTML Reference endpoints.
     *
     * Defines routes for HTML entity search using functional handlers.
     */
    @Bean
    fun htmlReferenceRouter(searchHtmlEntitiesUseCase: SearchHtmlEntitiesUseCase): RouterFunction<ServerResponse> =
        coRouter {
            applicationProperties.apiBasePath.nest {
                accept(MediaType.APPLICATION_JSON).nest {
                    GET("/html-entities") { request ->
                        handleSearchHtmlEntities(request, searchHtmlEntitiesUseCase)
                    }
                }
            }
        }
}

/**
 * Converts database entity to domain entity.
 *
 * Returns a Result to handle potential validation errors in a pure functional way.
 * Uses Result type composition with expression chains to avoid intermediate variables
 * and early returns, achieving complete functional purity.
 *
 * @return Result containing HtmlEntity if successful, or error if validation fails
 */
private fun HtmlEntityDbEntity.toDomain(): Result<HtmlEntity> =
    runCatching { requireNotNull(id) { "HTML entity ID cannot be null" } }
        .flatMap { validId ->
            EntityCode
                .of(code)
                .flatMap { validCode ->
                    (code2?.let { EntityCode.of(it) } ?: Result.success(null))
                        .map { validCode2 ->
                            HtmlEntity(
                                id = validId,
                                name = name,
                                code = validCode,
                                code2 = validCode2,
                                standard = standard,
                                dtd = dtd,
                                description = description,
                            )
                        }
                }
        }
