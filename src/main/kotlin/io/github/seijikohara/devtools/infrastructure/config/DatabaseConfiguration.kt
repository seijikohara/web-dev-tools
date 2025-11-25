package io.github.seijikohara.devtools.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.flyway.autoconfigure.FlywayDataSource
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

/**
 * Database configuration for Flyway migrations.
 *
 * In Spring Boot 4 with R2DBC, DataSourceAutoConfiguration is disabled when
 * ConnectionFactory bean exists. This configuration explicitly creates a DataSource
 * bean for Flyway migrations to use.
 */
@Configuration
class DatabaseConfiguration {
    /**
     * Creates DataSource properties from application configuration.
     *
     * @return [DataSourceProperties] instance
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    fun dataSourceProperties(): DataSourceProperties = DataSourceProperties()

    /**
     * Creates a DataSource bean for Flyway migrations.
     *
     * @param properties DataSource properties
     * @return [DataSource] instance
     */
    @Bean
    @FlywayDataSource
    fun dataSource(properties: DataSourceProperties): DataSource = properties.initializeDataSourceBuilder().build()
}
