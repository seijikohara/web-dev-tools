package io.github.seijikohara.devtools.infrastructure.web.dto

/**
 * HTTP headers response.
 *
 * @property headers List of HTTP headers
 */
data class HttpHeadersResponseDto(
    val headers: List<HttpHeaderDto>,
) {
    /**
     * HTTP header.
     *
     * @property name Header name
     * @property value Header value
     */
    data class HttpHeaderDto(
        val name: String,
        val value: String,
    )
}
