package io.github.seijikohara.devtools.infrastructure.web.handler

import io.github.seijikohara.devtools.infrastructure.config.ApplicationProperties
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

/**
 * Handler function for index page endpoint.
 *
 * @param request The server request
 * @param applicationProperties Application properties containing index file content
 * @return ServerResponse with HTML content
 */
suspend fun handleIndex(
    request: ServerRequest,
    applicationProperties: ApplicationProperties,
): ServerResponse =
    ServerResponse
        .ok()
        .contentType(MediaType.TEXT_HTML)
        .bodyValueAndAwait(applicationProperties.indexFile)
