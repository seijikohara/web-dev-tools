package io.github.seijikohara.devtools.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

/**
 * WebFlux configuration class for configuring web layer settings.
 *
 * @property applicationProperties Application configuration properties
 */
@Configuration
class WebFluxConfig(
    val applicationProperties: ApplicationProperties,
) : WebFluxConfigurer {
    /**
     * Configures CORS (Cross-Origin Resource Sharing) mappings.
     *
     * Applies CORS settings from application properties to the specified path patterns.
     *
     * @param corsRegistry Registry for CORS configuration
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
