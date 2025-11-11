package io.github.seijikohara.devtools.infrastructure.web.config

import io.github.seijikohara.devtools.application.usecase.GetIpInfoUseCase
import io.github.seijikohara.devtools.infrastructure.config.ApplicationProperties
import io.github.seijikohara.devtools.infrastructure.web.handler.handleGetIpInfo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import io.github.seijikohara.devtools.application.usecase.getIpInfoUseCase as createGetIpInfoUseCase

/**
 * Configuration for IP information endpoint.
 *
 * This configuration defines the routing and use case for retrieving
 * client IP address and hostname information.
 */
@Configuration
class IpInfoConfiguration(
    private val applicationProperties: ApplicationProperties,
) {
    /**
     * Creates a GetIpInfoUseCase bean.
     *
     * @return GetIpInfoUseCase instance
     */
    @Bean
    fun getIpInfoUseCase(): GetIpInfoUseCase = createGetIpInfoUseCase()

    /**
     * Router for IP information endpoint.
     *
     * Defines the route for retrieving client IP information.
     */
    @Bean
    fun ipInfoRouter(getIpInfoUseCase: GetIpInfoUseCase): RouterFunction<ServerResponse> =
        coRouter {
            applicationProperties.apiBasePath.nest {
                accept(MediaType.APPLICATION_JSON).nest {
                    GET("/ip") { request ->
                        handleGetIpInfo(request, getIpInfoUseCase)
                    }
                }
            }
        }
}
