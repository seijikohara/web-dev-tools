package io.github.seijikohara.devtools.infrastructure.web.dto

import io.github.seijikohara.devtools.application.usecase.SearchHtmlEntitiesUseCase
import io.github.seijikohara.devtools.domain.htmlreference.model.HtmlEntity

/**
 * Data transfer object for HTML entity search response.
 *
 * Represents paginated search results for HTML entities.
 *
 * @property content List of HTML entities in the current page
 * @property totalElements Total number of entities matching the search criteria
 * @property page Current page number (zero-based)
 * @property size Number of items per page
 * @property totalPages Total number of pages
 */
data class HtmlEntitySearchResponseDto(
    val content: List<HtmlEntityDto>,
    val totalElements: Long,
    val page: Int,
    val size: Int,
    val totalPages: Int,
) {
    /**
     * Data transfer object for individual HTML entity.
     *
     * @property name Name of the HTML entity
     * @property code Primary numeric character code
     * @property code2 Secondary numeric character code, if applicable
     * @property standard HTML standard specification
     * @property dtd Document type definition
     * @property description Human-readable description of the entity
     * @property entityReference HTML entity reference string (e.g., "&amp;")
     */
    data class HtmlEntityDto(
        val name: String,
        val code: Long,
        val code2: Long?,
        val standard: String?,
        val dtd: String?,
        val description: String?,
        val entityReference: String,
    )
}

/**
 * Converts use case response to DTO.
 *
 * @receiver Response from the search HTML entities use case
 * @return HTML entity search response DTO
 */
fun SearchHtmlEntitiesUseCase.Response.toDto(): HtmlEntitySearchResponseDto =
    HtmlEntitySearchResponseDto(
        content = entities.map { it.toDto() },
        totalElements = totalCount,
        page = page,
        size = size,
        totalPages = ((totalCount + size - 1) / size).toInt(),
    )

/**
 * Converts domain entity to DTO.
 *
 * @receiver HTML entity domain object
 * @return HTML entity DTO
 */
private fun HtmlEntity.toDto(): HtmlEntitySearchResponseDto.HtmlEntityDto =
    HtmlEntitySearchResponseDto.HtmlEntityDto(
        name = name,
        code = code.value,
        code2 = code2?.value,
        standard = standard,
        dtd = dtd,
        description = description,
        entityReference = entityReference(),
    )
