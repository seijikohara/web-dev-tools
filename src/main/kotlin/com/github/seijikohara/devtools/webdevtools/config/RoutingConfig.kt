package com.github.seijikohara.devtools.webdevtools.config

import com.github.seijikohara.devtools.webdevtools.handler.GeoApiHandler
import com.github.seijikohara.devtools.webdevtools.handler.HtmlEntitiesApiHandler
import com.github.seijikohara.devtools.webdevtools.handler.HttpHeadersApiHandler
import com.github.seijikohara.devtools.webdevtools.handler.IndexHandler
import com.github.seijikohara.devtools.webdevtools.handler.IpApiHandler
import com.github.seijikohara.devtools.webdevtools.handler.RdapApiHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RoutingConfig(
    private val applicationProperties: ApplicationProperties,
    private val indexHandler: IndexHandler,
    private val ipApiHandler: IpApiHandler,
    private val rdapApiHandler: RdapApiHandler,
    private val geoApiHandler: GeoApiHandler,
    private val httpHeadersApiHandler: HttpHeadersApiHandler,
    private val htmlEntitiesApiHandler: HtmlEntitiesApiHandler,
) {
    @Bean
    fun apiRouter() =
        coRouter {
            accept(MediaType.ALL).nest {
                GET("/*", indexHandler::getIndex)
                applicationProperties.apiBasePath.nest {
                    GET("/ip", ipApiHandler::getIp)
                    GET("/rdap/{ip}", rdapApiHandler::getRdap)
                    GET("/geo/{ip}", geoApiHandler::getGeo)
                    GET("/http-headers", httpHeadersApiHandler::getHttpHeaders)
                    GET("/html-entities", htmlEntitiesApiHandler::getHtmlEntities)
                }
            }
        }
}
