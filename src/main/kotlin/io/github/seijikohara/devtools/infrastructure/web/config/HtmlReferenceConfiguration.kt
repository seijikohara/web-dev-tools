package io.github.seijikohara.devtools.infrastructure.web.config

import io.github.seijikohara.devtools.application.usecase.SearchHtmlEntitiesUseCase
import io.github.seijikohara.devtools.domain.htmlreference.repository.HtmlEntityRepository
import io.github.seijikohara.devtools.infrastructure.config.ApplicationProperties
import io.github.seijikohara.devtools.infrastructure.database.htmlreference.HtmlEntityDbRepository
import io.github.seijikohara.devtools.infrastructure.database.htmlreference.HtmlEntityRepositoryAdapter
import io.github.seijikohara.devtools.infrastructure.web.handler.handleSearchHtmlEntities
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
     * Repository for HTML entities.
     *
     * Adapter implementation bridges domain layer with database infrastructure.
     */
    @Bean
    fun htmlEntityRepository(dbRepository: HtmlEntityDbRepository): HtmlEntityRepository = HtmlEntityRepositoryAdapter(dbRepository)

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
