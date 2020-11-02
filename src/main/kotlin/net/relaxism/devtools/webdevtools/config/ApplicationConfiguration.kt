package net.relaxism.devtools.webdevtools.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(ApplicationProperties::class)
class ApplicationConfiguration {

    @Bean
    fun webClient(): WebClient {
        return WebClient.builder().build();
    }

}

@ConstructorBinding
@ConfigurationProperties(prefix = "application")
data class ApplicationProperties(
    val cors: CorsProperties,
    val network: NetworkProperties
) {

    data class CorsProperties(
        val mappingPathPattern: String,
        val allowedOrigins: List<String>,
        val allowedMethods: List<String>,
        val maxAge: Long
    )

    data class NetworkProperties(
        val rdap: RdapProperties,
        val geo: GeoProperties
    ) {

        data class RdapProperties(val uri: String)
        data class GeoProperties(val uri: String)

    }

}

