package io.github.seijikohara.devtools.infrastructure.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for OpenAPI documentation.
 */
@Configuration
class OpenApiConfiguration(
    private val buildProperties: BuildProperties,
) {
    /**
     * Creates OpenAPI documentation configuration.
     *
     * @return [OpenAPI] configuration instance
     */
    @Bean
    fun openApi(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("${buildProperties.name} API")
                    .description("API for ${buildProperties.name}")
                    .version(buildProperties.version),
            )
}
