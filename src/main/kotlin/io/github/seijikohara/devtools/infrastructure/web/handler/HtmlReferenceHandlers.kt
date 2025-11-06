package io.github.seijikohara.devtools.infrastructure.web.handler

import io.github.seijikohara.devtools.application.usecase.SearchHtmlEntitiesUseCase
import io.github.seijikohara.devtools.infrastructure.web.dto.toDto
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait

/**
 * Handler function for HTML entity search endpoint.
 *
 * @param request The server request containing search parameters
 * @param useCase The use case for searching HTML entities
 * @return ServerResponse with search results or error response
 */
suspend fun handleSearchHtmlEntities(
    request: ServerRequest,
    useCase: SearchHtmlEntitiesUseCase,
): ServerResponse =
    useCase(
        SearchHtmlEntitiesUseCase.Request(
            name = request.queryParam("name").orElse(""),
            page = request.queryParam("page").map(String::toInt).orElse(0),
            size = request.queryParam("size").map(String::toInt).orElse(50),
        ),
    ).fold(
        onSuccess = { response ->
            ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(response.toDto())
        },
        onFailure = { error ->
            when (error) {
                is IllegalArgumentException ->
                    ServerResponse.badRequest().buildAndAwait()
                else -> throw error
            }
        },
    )
