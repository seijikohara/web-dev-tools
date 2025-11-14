package io.github.seijikohara.devtools.infrastructure.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

/**
 * WebFlux configuration for web layer settings.
 */
@Configuration
class WebFluxConfig(
    val applicationProperties: ApplicationProperties,
) : WebFluxConfigurer {
    /**
     * Configures CORS (Cross-Origin Resource Sharing) mappings.
     *
     * @param corsRegistry [CorsRegistry] for CORS configuration
     */
    override fun addCorsMappings(corsRegistry: CorsRegistry) {
        applicationProperties.cors.run {
            corsRegistry
                .addMapping(mappingPathPattern)
                .allowedOrigins(*allowedOrigins.toTypedArray())
                .allowedMethods(*allowedMethods.toTypedArray())
                .maxAge(maxAge)
        }
    }
}
