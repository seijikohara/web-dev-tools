package io.github.seijikohara.devtools

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * Main application class for the Web Development Tools application.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
class WebApplication

/**
 * Application entry point.
 *
 * @param args Command-line arguments passed to the application
 */
fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}
