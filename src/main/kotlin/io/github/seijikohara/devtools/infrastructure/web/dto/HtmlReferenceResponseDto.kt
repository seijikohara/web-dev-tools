package io.github.seijikohara.devtools.infrastructure.web.dto

import io.github.seijikohara.devtools.application.usecase.SearchHtmlEntitiesUseCase
import io.github.seijikohara.devtools.domain.htmlreference.model.HtmlEntity

/**
 * HTML entity search response.
 *
 * @property content List of HTML entities
 * @property totalElements Total number of entities
 * @property page Page number
 * @property size Items per page
 * @property totalPages Total pages
 */
data class HtmlEntitySearchResponseDto(
    val content: List<HtmlEntityDto>,
    val totalElements: Long,
    val page: Int,
    val size: Int,
    val totalPages: Int,
) {
    /**
     * HTML entity.
     *
     * @property name Entity name
     * @property code Numeric character code
     * @property code2 Alternative numeric character code
     * @property standard HTML standard specification
     * @property dtd Document type definition
     * @property description Entity description
     * @property entityReference Entity reference string
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
 * Converts to [HtmlEntitySearchResponseDto].
 *
 * @receiver Response data
 * @return [HtmlEntitySearchResponseDto] instance
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
 * Converts to [HtmlEntityDto].
 *
 * @receiver HTML entity
 * @return [HtmlEntityDto] instance
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
