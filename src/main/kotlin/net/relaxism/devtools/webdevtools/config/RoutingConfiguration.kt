package net.relaxism.devtools.webdevtools.config

import net.relaxism.devtools.webdevtools.handler.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@Configuration
class RoutingConfiguration(
    @Autowired private val applicationProperties: ApplicationProperties,
    @Autowired private val indexHandler: IndexHandler,
    @Autowired private val ipApiHandler: IpApiHandler,
    @Autowired private val rdapApiHandler: RdapApiHandler,
    @Autowired private val geoApiHandler: GeoApiHandler,
    @Autowired private val httpHeadersApiHandler: HttpHeadersApiHandler,
    @Autowired private val htmlEntitiesApiHandler: HtmlEntitiesApiHandler,
) {

    @Bean
    fun apiRouter() = router {
        val apiBasePath = applicationProperties.apiBasePath
        accept(MediaType.ALL).nest {
            GET("/*", indexHandler::getIndex)
            GET("${apiBasePath}/ip", ipApiHandler::getIp)
            GET("${apiBasePath}/rdap/{ip}", rdapApiHandler::getRdap)
            GET("${apiBasePath}/geo/{ip}", geoApiHandler::getGeo)
            GET("${apiBasePath}/http-headers", httpHeadersApiHandler::getHttpHeaders)
            GET("${apiBasePath}/html-entities", htmlEntitiesApiHandler::getHtmlEntities)
        }
    }

}
