package io.github.seijikohara.devtools.infrastructure.web.config

import io.github.seijikohara.devtools.config.ApplicationProperties
import io.github.seijikohara.devtools.infrastructure.web.handler.handleIndex
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

/**
 * Configuration for Index page.
 *
 * Defines routing for the application's index page.
 */
@Configuration
class IndexConfiguration(
    private val applicationProperties: ApplicationProperties,
) {
    /**
     * Router for Index page endpoint.
     *
     * Defines the catch-all route for serving the application's index page.
     *
     * @return Router function for index page
     */
    @Bean
    fun indexRouter(): RouterFunction<ServerResponse> =
        coRouter {
            accept(MediaType.ALL).nest {
                GET("/*") { request ->
                    handleIndex(request, applicationProperties)
                }
            }
        }
}
