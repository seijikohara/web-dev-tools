package io.github.seijikohara.devtools.infrastructure.config

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

/**
 * Configuration class for application-wide beans and settings.
 */
@Configuration
@EnableConfigurationProperties(ApplicationProperties::class)
class ApplicationConfiguration {
    companion object {
        private val logger = LoggerFactory.getLogger(ApplicationConfiguration::class.java)
    }

    /**
     * Creates a [WebClient] bean.
     *
     * @return [WebClient] instance
     */
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

/**
 * Application configuration properties.
 */
@ConfigurationProperties(prefix = "application")
data class ApplicationProperties(
    val apiBasePath: String,
    val indexFile: Resource,
    val swaggerUiPaths: List<String>,
    val cors: CorsProperties,
    val network: NetworkProperties,
) {
    /**
     * CORS (Cross-Origin Resource Sharing) configuration properties.
     *
     * @property mappingPathPattern URL path pattern to apply CORS configuration
     * @property allowedOrigins List of allowed origin URLs
     * @property allowedMethods List of allowed HTTP methods
     * @property maxAge Maximum age in seconds for preflight cache
     */
    data class CorsProperties(
        val mappingPathPattern: String,
        val allowedOrigins: List<String>,
        val allowedMethods: List<String>,
        val maxAge: Long,
    )

    /**
     * Network-related configuration properties.
     *
     * @property rdap RDAP service configuration
     * @property geo Geo-location service configuration
     * @property dns DNS resolution service configuration
     */
    data class NetworkProperties(
        val rdap: RdapProperties,
        val geo: GeoProperties,
        val dns: DnsProperties,
    ) {
        /**
         * RDAP (Registration Data Access Protocol) service configuration.
         *
         * @property ipv4 Resource reference to IPv4 RDAP bootstrap file
         * @property ipv6 Resource reference to IPv6 RDAP bootstrap file
         */
        data class RdapProperties(
            val ipv4: Resource,
            val ipv6: Resource,
        )

        /**
         * Geo-location service configuration.
         *
         * @property uri URI of the geo-location service endpoint
         */
        data class GeoProperties(
            val uri: String,
        )

        /**
         * DNS resolution service configuration.
         *
         * @property uri URI of the DNS-over-HTTPS service endpoint
         */
        data class DnsProperties(
            val uri: String,
        )
    }
}
