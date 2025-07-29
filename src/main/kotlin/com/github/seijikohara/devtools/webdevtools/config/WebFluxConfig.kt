package com.github.seijikohara.devtools.webdevtools.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class WebFluxConfig(
    val applicationProperties: ApplicationProperties,
) : WebFluxConfigurer {
    override fun addCorsMappings(corsRegistry: CorsRegistry) {
        applicationProperties.cors.let { corsProperties ->
            corsRegistry
                .addMapping(corsProperties.mappingPathPattern)
                .allowedOrigins(*corsProperties.allowedOrigins.toTypedArray())
                .allowedMethods(*corsProperties.allowedMethods.toTypedArray())
                .maxAge(corsProperties.maxAge)
        }
    }
}
