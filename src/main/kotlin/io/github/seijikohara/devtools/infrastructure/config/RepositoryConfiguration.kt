package io.github.seijikohara.devtools.infrastructure.config

import io.github.seijikohara.devtools.domain.htmlreference.repository.HtmlEntityRepository
import io.github.seijikohara.devtools.domain.networkinfo.repository.GeoIpRepository
import io.github.seijikohara.devtools.domain.networkinfo.repository.RdapRepository
import io.github.seijikohara.devtools.domain.networkinfo.repository.RdapServerResolver
import io.github.seijikohara.devtools.infrastructure.database.htmlreference.HtmlEntityDbRepository
import io.github.seijikohara.devtools.infrastructure.database.htmlreference.HtmlEntityRepositoryAdapter
import io.github.seijikohara.devtools.infrastructure.externalapi.geoip.GeoIpRepositoryAdapter
import io.github.seijikohara.devtools.infrastructure.externalapi.rdap.RdapRepositoryAdapter
import io.github.seijikohara.devtools.infrastructure.externalapi.rdap.RdapServerResolverAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

/**
 * Configuration for repository implementations.
 */
@Configuration
class RepositoryConfiguration(
    private val applicationProperties: ApplicationProperties,
) {
    /**
     * Creates [RdapServerResolver] bean.
     *
     * @return [RdapServerResolver] instance
     */
    @Bean
    fun rdapServerResolver(): RdapServerResolver = RdapServerResolverAdapter(applicationProperties)

    /**
     * Creates [RdapRepository] bean.
     *
     * @return [RdapRepository] instance
     */
    @Bean
    fun rdapRepository(
        webClient: WebClient,
        rdapServerResolver: RdapServerResolver,
    ): RdapRepository = RdapRepositoryAdapter(webClient, rdapServerResolver)

    /**
     * Creates [GeoIpRepository] bean.
     *
     * @return [GeoIpRepository] instance
     */
    @Bean
    fun geoIpRepository(webClient: WebClient): GeoIpRepository = GeoIpRepositoryAdapter(webClient, applicationProperties)

    /**
     * Creates [HtmlEntityRepository] bean.
     *
     * @return [HtmlEntityRepository] instance
     */
    @Bean
    fun htmlEntityRepository(dbRepository: HtmlEntityDbRepository): HtmlEntityRepository = HtmlEntityRepositoryAdapter(dbRepository)
}
