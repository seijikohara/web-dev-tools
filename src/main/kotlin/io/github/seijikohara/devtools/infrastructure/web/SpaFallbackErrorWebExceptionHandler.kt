package io.github.seijikohara.devtools.infrastructure.web

import io.github.seijikohara.devtools.infrastructure.config.ApplicationProperties
import org.springdoc.core.properties.SpringDocConfigProperties
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ResponseStatusException

/**
 * Error handler for SPA (Single Page Application) fallback routing.
 *
 * Intercepts 404 errors for HTML requests and serves the SPA's index.html to enable
 * client-side routing. API, OpenAPI, and Actuator endpoints are excluded from fallback
 * and return their actual 404 errors.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
class SpaFallbackErrorWebExceptionHandler(
    errorAttributes: ErrorAttributes,
    webProperties: WebProperties,
    applicationContext: ApplicationContext,
    private val applicationProperties: ApplicationProperties,
    private val springDocProperties: SpringDocConfigProperties,
    private val webEndpointProperties: WebEndpointProperties,
    serverCodecConfigurer: ServerCodecConfigurer,
) : AbstractErrorWebExceptionHandler(errorAttributes, webProperties.resources, applicationContext) {
    /**
     * Path prefixes excluded from SPA fallback.
     */
    private val excludedPaths: List<String> =
        buildList {
            add(applicationProperties.apiBasePath)
            add(springDocProperties.apiDocs.path)
            addAll(applicationProperties.swaggerUiPaths)
            add(webEndpointProperties.basePath)
        }

    init {
        serverCodecConfigurer.run {
            setMessageWriters(writers)
            setMessageReaders(readers)
        }
    }

    override fun getRoutingFunction(errorAttributes: ErrorAttributes): RouterFunction<ServerResponse> =
        route(::isSpaNotFoundRequest) {
            ServerResponse
                .ok()
                .contentType(MediaType.TEXT_HTML)
                .bodyValue(applicationProperties.indexFile)
        }

    /**
     * Determines if a request should receive the SPA fallback response.
     *
     * @param request The server request
     * @return true if this is a SPA route request, false otherwise
     */
    private fun isSpaNotFoundRequest(request: ServerRequest): Boolean {
        val error = getError(request)
        val isExcludedPath =
            excludedPaths.any { request.path().pathStartsWith(it) }

        return error is ResponseStatusException &&
            error.statusCode == HttpStatus.NOT_FOUND &&
            !isExcludedPath &&
            request.headers().accept().any { it.includes(MediaType.TEXT_HTML) }
    }

    /**
     * Checks if this path starts with the given prefix, handling trailing slashes.
     *
     * @param prefix The path prefix to check
     * @return true if this path starts with the prefix
     */
    private fun String.pathStartsWith(prefix: String): Boolean {
        val normalizedPath = trimEnd('/')
        val normalizedPrefix = prefix.trimEnd('/')

        return normalizedPath == normalizedPrefix ||
            normalizedPath.startsWith("$normalizedPrefix/")
    }
}
