package io.github.seijikohara.devtools.infrastructure.web.controller

import io.github.seijikohara.devtools.application.usecase.SearchHtmlEntitiesUseCase
import io.github.seijikohara.devtools.infrastructure.web.dto.HtmlEntitySearchResponseDto
import io.github.seijikohara.devtools.infrastructure.web.dto.toDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

/**
 * REST controller for HTML Reference endpoints.
 */
@RestController
@RequestMapping("\${application.api-base-path}")
@Tag(name = "HTML Reference")
class HtmlReferenceController(
    private val searchHtmlEntitiesUseCase: SearchHtmlEntitiesUseCase,
) {
    /**
     * Searches HTML entities.
     *
     * @param name Name to search for
     * @param page Page number
     * @param size Items per page
     * @return [HtmlEntitySearchResponseDto] instance
     */
    @GetMapping("/html-entities")
    @Operation(summary = "Search HTML entities")
    suspend fun searchHtmlEntities(
        @Parameter(description = "Name to search for", example = "amp")
        @RequestParam(defaultValue = "")
        name: String,
        @Parameter(description = "Page number (zero-based)", example = "0")
        @RequestParam(defaultValue = "0")
        page: Int,
        @Parameter(description = "Number of items per page", example = "50")
        @RequestParam(defaultValue = "50")
        size: Int,
    ): HtmlEntitySearchResponseDto =
        searchHtmlEntitiesUseCase(
            SearchHtmlEntitiesUseCase.Request(
                name = name,
                page = page,
                size = size,
            ),
        ).fold(
            onSuccess = { it.toDto() },
            onFailure = { error ->
                when (error) {
                    is IllegalArgumentException ->
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, error.message)
                    else -> throw error
                }
            },
        )
}
