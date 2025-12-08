package io.github.seijikohara.devtools.infrastructure.config

import io.github.seijikohara.devtools.application.usecase.GetGeoLocationUseCase
import io.github.seijikohara.devtools.application.usecase.GetHttpHeadersUseCase
import io.github.seijikohara.devtools.application.usecase.GetIpInfoUseCase
import io.github.seijikohara.devtools.application.usecase.GetRdapInformationUseCase
import io.github.seijikohara.devtools.application.usecase.ResolveDnsUseCase
import io.github.seijikohara.devtools.application.usecase.SearchHtmlEntitiesUseCase
import io.github.seijikohara.devtools.domain.dns.repository.DnsRepository
import io.github.seijikohara.devtools.domain.htmlreference.repository.HtmlEntityRepository
import io.github.seijikohara.devtools.domain.networkinfo.repository.GeoIpRepository
import io.github.seijikohara.devtools.domain.networkinfo.repository.RdapRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import io.github.seijikohara.devtools.application.usecase.getGeoLocationUseCase as createGetGeoLocationUseCase
import io.github.seijikohara.devtools.application.usecase.getHttpHeadersUseCase as createGetHttpHeadersUseCase
import io.github.seijikohara.devtools.application.usecase.getIpInfoUseCase as createGetIpInfoUseCase
import io.github.seijikohara.devtools.application.usecase.getRdapInformationUseCase as createGetRdapInformationUseCase
import io.github.seijikohara.devtools.application.usecase.resolveDnsUseCase as createResolveDnsUseCase
import io.github.seijikohara.devtools.application.usecase.searchHtmlEntitiesUseCase as createSearchHtmlEntitiesUseCase

/**
 * Configuration for application use cases.
 */
@Configuration
class UseCaseConfiguration {
    @Bean
    fun getIpInfoUseCase(): GetIpInfoUseCase = createGetIpInfoUseCase()

    @Bean
    fun getHttpHeadersUseCase(): GetHttpHeadersUseCase = createGetHttpHeadersUseCase()

    @Bean
    fun getRdapInformationUseCase(rdapRepository: RdapRepository): GetRdapInformationUseCase =
        createGetRdapInformationUseCase(rdapRepository)

    @Bean
    fun getGeoLocationUseCase(geoIpRepository: GeoIpRepository): GetGeoLocationUseCase = createGetGeoLocationUseCase(geoIpRepository)

    @Bean
    fun searchHtmlEntitiesUseCase(htmlEntityRepository: HtmlEntityRepository): SearchHtmlEntitiesUseCase =
        createSearchHtmlEntitiesUseCase(htmlEntityRepository)

    @Bean
    fun resolveDnsUseCase(dnsRepository: DnsRepository): ResolveDnsUseCase = createResolveDnsUseCase(dnsRepository)
}
