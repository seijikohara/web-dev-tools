package io.github.seijikohara.devtools

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * Main application class for the Web Development Tools application.
 *
 * This Spring Boot application provides various web development utilities
 * including HTML entity reference, network information lookup, and HTTP diagnostics.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
class WebApplication

/**
 * Application entry point.
 *
 * Bootstraps the Spring Boot application with the provided command-line arguments.
 *
 * @param args Command-line arguments passed to the application
 */
fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}
