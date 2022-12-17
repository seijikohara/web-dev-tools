package net.relaxism.devtools.webdevtools.config

import net.relaxism.devtools.webdevtools.handler.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RoutingConfig(
    @Autowired private val applicationProperties: ApplicationProperties,
    @Autowired private val indexHandler: IndexHandler,
    @Autowired private val ipApiHandler: IpApiHandler,
    @Autowired private val rdapApiHandler: RdapApiHandler,
    @Autowired private val geoApiHandler: GeoApiHandler,
    @Autowired private val httpHeadersApiHandler: HttpHeadersApiHandler,
    @Autowired private val htmlEntitiesApiHandler: HtmlEntitiesApiHandler,
) {

    @Bean
    fun apiRouter() = coRouter {
        accept(MediaType.ALL).nest {
            GET("/*", indexHandler::getIndex)
            (applicationProperties.apiBasePath).nest {
                GET("/ip", ipApiHandler::getIp)
                GET("/rdap/{ip}", rdapApiHandler::getRdap)
                GET("/geo/{ip}", geoApiHandler::getGeo)
                GET("/http-headers", httpHeadersApiHandler::getHttpHeaders)
                GET("/html-entities", htmlEntitiesApiHandler::getHtmlEntities)
            }
        }

    }

}
