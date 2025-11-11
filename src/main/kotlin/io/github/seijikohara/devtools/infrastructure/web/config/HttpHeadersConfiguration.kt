package io.github.seijikohara.devtools.infrastructure.web.config

import io.github.seijikohara.devtools.application.usecase.GetHttpHeadersUseCase
import io.github.seijikohara.devtools.infrastructure.config.ApplicationProperties
import io.github.seijikohara.devtools.infrastructure.web.handler.handleGetHttpHeaders
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import io.github.seijikohara.devtools.application.usecase.getHttpHeadersUseCase as createGetHttpHeadersUseCase

/**
 * Configuration for HTTP headers endpoint.
 *
 * This configuration defines the routing and use case for retrieving
 * HTTP request headers.
 */
@Configuration
class HttpHeadersConfiguration(
    private val applicationProperties: ApplicationProperties,
) {
    /**
     * Creates a GetHttpHeadersUseCase bean.
     *
     * @return GetHttpHeadersUseCase instance
     */
    @Bean
    fun getHttpHeadersUseCase(): GetHttpHeadersUseCase = createGetHttpHeadersUseCase()

    /**
     * Router for HTTP headers endpoint.
     *
     * Defines the route for retrieving HTTP request headers.
     */
    @Bean
    fun httpHeadersRouter(getHttpHeadersUseCase: GetHttpHeadersUseCase): RouterFunction<ServerResponse> =
        coRouter {
            applicationProperties.apiBasePath.nest {
                accept(MediaType.APPLICATION_JSON).nest {
                    GET("/http-headers") { request ->
                        handleGetHttpHeaders(request, getHttpHeadersUseCase)
                    }
                }
            }
        }
}
