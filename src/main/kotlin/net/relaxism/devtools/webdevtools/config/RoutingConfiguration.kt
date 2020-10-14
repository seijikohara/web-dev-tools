package net.relaxism.devtools.webdevtools.config

import net.relaxism.devtools.webdevtools.handler.GeoApiHandler
import net.relaxism.devtools.webdevtools.handler.HttpHeadersApiHandler
import net.relaxism.devtools.webdevtools.handler.IpApiHandler
import net.relaxism.devtools.webdevtools.handler.RdapApiHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router


@Configuration
class RoutingConfiguration(
    @Autowired private val ipApiHandler: IpApiHandler,
    @Autowired private val rdapApiHandler: RdapApiHandler,
    @Autowired private val geoApiHandler: GeoApiHandler,
    @Autowired private val httpHeadersApiHandler: HttpHeadersApiHandler
) {

    @Bean
    fun apiRouter() = router {
        accept(MediaType.ALL).nest {
            GET("/api/ip", ipApiHandler::getIp)
            GET("/api/rdap/{ip}", rdapApiHandler::getRdap)
            GET("/api/geo/{ip}", geoApiHandler::getGeo)
            GET("/api/http-headers", httpHeadersApiHandler::getHttpHeaders)
        }
    }

}
