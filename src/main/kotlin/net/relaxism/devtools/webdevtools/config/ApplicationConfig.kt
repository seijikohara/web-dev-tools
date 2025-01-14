package net.relaxism.devtools.webdevtools.config

import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
@EnableConfigurationProperties(ApplicationProperties::class)
class ApplicationConfiguration(
    @Autowired private val logger: Logger,
) {
    @Bean
    fun webClient(): WebClient =
        WebClient
            .builder()
            .filter { clientRequest, next ->
                logger.info("External Request to ${clientRequest.method()} ${clientRequest.url()} headers=${clientRequest.headers()}")
                next.exchange(clientRequest)
            }.clientConnector(
                ReactorClientHttpConnector(
                    HttpClient.create().followRedirect(true),
                ),
            ).build()
}

@ConfigurationProperties(prefix = "application")
data class ApplicationProperties(
    val apiBasePath: String,
    val indexFile: Resource,
    val cors: CorsProperties,
    val network: NetworkProperties,
) {
    data class CorsProperties(
        val mappingPathPattern: String,
        val allowedOrigins: List<String>,
        val allowedMethods: List<String>,
        val maxAge: Long,
    )

    data class NetworkProperties(
        val rdap: RdapProperties,
        val geo: GeoProperties,
    ) {
        data class RdapProperties(
            val ipv4: Resource,
            val ipv6: Resource,
        )

        data class GeoProperties(
            val uri: String,
        )
    }
}
