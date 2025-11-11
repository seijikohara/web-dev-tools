package io.github.seijikohara.devtools.application.usecase

/**
 * Use case for retrieving HTTP request headers.
 *
 * This is a functional interface that encapsulates the business logic
 * for extracting and formatting HTTP headers from a request.
 */
fun interface GetHttpHeadersUseCase {
    /**
     * Executes the use case.
     *
     * @param request The use case request containing HTTP headers
     * @return Response containing the list of HTTP headers
     */
    operator fun invoke(request: Request): Response

    /**
     * Request for retrieving HTTP headers.
     *
     * @property headers Map of header names to their values (multi-value headers supported)
     */
    data class Request(
        val headers: Map<String, List<String>>,
    )

    /**
     * Response containing HTTP headers.
     *
     * @property headers List of HTTP header name-value pairs
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
 * Factory function to create a GetHttpHeadersUseCase instance.
 *
 * Flattens multi-value headers into individual name-value pairs.
 *
 * @return A GetHttpHeadersUseCase instance
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
