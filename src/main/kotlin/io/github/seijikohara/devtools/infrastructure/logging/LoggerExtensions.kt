package io.github.seijikohara.devtools.infrastructure.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Extension property to get a Logger instance for any class.
 *
 * This provides a simple and idiomatic way to access SLF4J loggers without
 * dependency injection. The logger is automatically configured with the
 * class name of the caller.
 *
 * Usage:
 * ```
 * class MyService {
 *     fun process() {
 *         logger.info("Processing started")
 *         logger.debug("Debug information")
 *     }
 * }
 * ```
 *
 * @return Logger instance configured for the calling class
 */
val Any.logger: Logger
    get() = LoggerFactory.getLogger(javaClass)
