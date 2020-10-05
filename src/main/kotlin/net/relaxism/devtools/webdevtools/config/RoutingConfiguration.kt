package net.relaxism.devtools.webdevtools.config

import net.relaxism.devtools.webdevtools.handler.IpApiHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router


@Configuration
class RoutingConfiguration(private val ipApiHandler: IpApiHandler) {

    @Bean
    fun apiRouter() = router {
        accept(MediaType.ALL).nest {
            GET("/api/ip", ipApiHandler::getIp)
        }
    }

}
