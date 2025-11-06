package io.github.seijikohara.devtools.infrastructure.web.config

import io.github.seijikohara.devtools.application.usecase.GetGeoLocationUseCase
import io.github.seijikohara.devtools.application.usecase.GetRdapInformationUseCase
import io.github.seijikohara.devtools.config.ApplicationProperties
import io.github.seijikohara.devtools.domain.networkinfo.repository.GeoIpRepository
import io.github.seijikohara.devtools.domain.networkinfo.repository.RdapRepository
import io.github.seijikohara.devtools.infrastructure.externalapi.geoip.GeoIpRepositoryAdapter
import io.github.seijikohara.devtools.infrastructure.externalapi.rdap.RdapRepositoryAdapter
import io.github.seijikohara.devtools.infrastructure.externalapi.rdap.RdapServerResolver
import io.github.seijikohara.devtools.infrastructure.externalapi.rdap.RdapServerResolverAdapter
import io.github.seijikohara.devtools.infrastructure.web.handler.handleGetGeoLocation
import io.github.seijikohara.devtools.infrastructure.web.handler.handleGetRdapInformation
import org.slf4j.Logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import io.github.seijikohara.devtools.application.usecase.getGeoLocationUseCase as createGetGeoLocationUseCase
import io.github.seijikohara.devtools.application.usecase.getRdapInformationUseCase as createGetRdapInformationUseCase

/**
 * Configuration for Network Information bounded context.
 *
 * Defines beans and routing for RDAP and GeoIP features following Clean Architecture principles.
 * This configuration layer is kept minimal, delegating actual implementation to Adapter classes.
 */
@Configuration
class NetworkInfoConfiguration(
    private val applicationProperties: ApplicationProperties,
) {
    /**
     * Resolver for finding the appropriate RDAP server URI for a given IP address.
     *
     * Uses RangeMap for efficient IP range lookups.
     */
    @Bean
    fun rdapServerResolver(logger: Logger): RdapServerResolver = RdapServerResolverAdapter(logger, applicationProperties)

    /**
     * Repository implementation for RDAP lookups.
     *
     * Retrieves RDAP data from external RDAP servers.
     */
    @Bean
    fun rdapRepository(
        logger: Logger,
        webClient: WebClient,
        rdapServerResolver: RdapServerResolver,
    ): RdapRepository = RdapRepositoryAdapter(logger, webClient, rdapServerResolver)

    /**
     * Repository implementation for GeoIP lookups.
     *
     * Retrieves geographic location data from external GeoIP services.
     */
    @Bean
    fun geoIpRepository(
        logger: Logger,
        webClient: WebClient,
    ): GeoIpRepository = GeoIpRepositoryAdapter(logger, webClient, applicationProperties)

    /**
     * Use case for retrieving RDAP information.
     *
     * Factory function pattern allows for composition without exposing implementation details.
     */
    @Bean
    fun getRdapInformationUseCase(rdapRepository: RdapRepository): GetRdapInformationUseCase =
        createGetRdapInformationUseCase(rdapRepository)

    /**
     * Use case for retrieving geographic location information.
     *
     * Factory function pattern allows for composition without exposing implementation details.
     */
    @Bean
    fun getGeoLocationUseCase(geoIpRepository: GeoIpRepository): GetGeoLocationUseCase = createGetGeoLocationUseCase(geoIpRepository)

    /**
     * Router for Network Information endpoints.
     *
     * Defines routes for RDAP and GeoIP lookups using functional handlers.
     */
    @Bean
    fun networkInfoRouter(
        getRdapInformationUseCase: GetRdapInformationUseCase,
        getGeoLocationUseCase: GetGeoLocationUseCase,
    ): RouterFunction<ServerResponse> =
        coRouter {
            applicationProperties.apiBasePath.nest {
                accept(MediaType.APPLICATION_JSON).nest {
                    GET("/rdap/{ip}") { request ->
                        handleGetRdapInformation(request, getRdapInformationUseCase)
                    }
                    GET("/geo/{ip}") { request ->
                        handleGetGeoLocation(request, getGeoLocationUseCase)
                    }
                }
            }
        }
}
