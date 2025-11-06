package io.github.seijikohara.devtools.infrastructure.web.dto

/**
 * Data transfer object for HTTP headers response.
 *
 * Represents the list of HTTP request headers received by the server.
 *
 * @property headers List of HTTP headers from the request
 */
data class HttpHeadersResponseDto(
    val headers: List<HttpHeaderDto>,
) {
    /**
     * Data transfer object for individual HTTP header.
     *
     * @property name HTTP header name
     * @property value HTTP header value
     */
    data class HttpHeaderDto(
        val name: String,
        val value: String,
    )
}
