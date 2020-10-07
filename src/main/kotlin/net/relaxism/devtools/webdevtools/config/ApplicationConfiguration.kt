package net.relaxism.devtools.webdevtools.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@EnableConfigurationProperties(ApplicationProperties::class)
class ApplicationConfiguration {

    @Bean
    fun restTemplate(): RestTemplate {
        val restTemplateBuilder = RestTemplateBuilder()
        return restTemplateBuilder.build()
    }

}

@ConstructorBinding
@ConfigurationProperties(prefix = "application")
data class ApplicationProperties(val network: NetworkProperties) {

    data class NetworkProperties(val rdap: RdapProperties, val geo: GeoProperties) {

        data class RdapProperties(val uri: String)
        data class GeoProperties(val uri: String)

    }

}

