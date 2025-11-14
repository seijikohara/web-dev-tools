package io.github.seijikohara.devtools.application.usecase

/**
 * Use case for retrieving HTTP request headers.
 *
 * @see Request
 * @see Response
 */
fun interface GetHttpHeadersUseCase {
    /**
     * Retrieves HTTP headers from the request.
     *
     * @param request The request containing HTTP headers
     * @return [Response] containing the list of HTTP headers
     */
    operator fun invoke(request: Request): Response

    /**
     * Request for retrieving HTTP headers.
     *
     * @property headers Map of header names to values supporting multi-value headers
     */
    data class Request(
        val headers: Map<String, List<String>>,
    )

    /**
     * Response containing HTTP headers.
     *
     * @property headers List of [HttpHeader] instances
     */
    data class Response(
        val headers: List<HttpHeader>,
    ) {
        /**
         * HTTP header name-value pair.
         *
         * @property name HTTP header name
         * @property value HTTP header value
         */
        data class HttpHeader(
            val name: String,
            val value: String,
        )
    }
}

/**
 * Creates a [GetHttpHeadersUseCase] instance.
 *
 * @return A [GetHttpHeadersUseCase] instance
 */
fun getHttpHeadersUseCase(): GetHttpHeadersUseCase =
    GetHttpHeadersUseCase { request ->
        GetHttpHeadersUseCase.Response(
            headers =
                request.headers
                    .flatMap { (name, values) ->
                        values.map { value ->
                            GetHttpHeadersUseCase.Response.HttpHeader(
                                name = name,
                                value = value,
                            )
                        }
                    },
        )
    }
