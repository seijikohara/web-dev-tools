package io.github.seijikohara.devtools.infrastructure.web.config

import io.github.seijikohara.devtools.config.ApplicationProperties
import io.github.seijikohara.devtools.infrastructure.web.handler.handleGetHttpHeaders
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

/**
 * Configuration for HTTP Diagnostics bounded context.
 *
 * This configuration defines the routing for HTTP diagnostics features
 * such as viewing request headers.
 */
@Configuration
class HttpDiagnosticsConfiguration(
    private val applicationProperties: ApplicationProperties,
) {
    /**
     * Router for HTTP Diagnostics endpoints.
     *
     * Defines routes for HTTP diagnostics features using functional handlers.
     */
    @Bean
    fun httpDiagnosticsRouter(): RouterFunction<ServerResponse> =
        coRouter {
            applicationProperties.apiBasePath.nest {
                accept(MediaType.APPLICATION_JSON).nest {
                    GET("/http-headers") { request ->
                        handleGetHttpHeaders(request)
                    }
                }
            }
        }
}
