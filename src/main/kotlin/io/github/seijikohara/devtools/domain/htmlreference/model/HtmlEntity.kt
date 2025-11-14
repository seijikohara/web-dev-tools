package io.github.seijikohara.devtools.domain.htmlreference.model

/**
 * Represents an HTML character entity.
 *
 * Encapsulates entity information including name, code points, and metadata.
 * Some entities require two code points for combining characters.
 *
 * @property id The unique identifier for this entity
 * @property name The entity name without delimiters
 * @property code The primary Unicode code point
 * @property code2 The secondary Unicode code point for entities requiring two codes, null for single-code entities
 * @property standard The HTML/XML standard that defines this entity, null if not specified
 * @property dtd The Document Type Definition that includes this entity, null if not applicable
 * @property description Human-readable description of the entity, null if not provided
 * @see EntityCode
 */
data class HtmlEntity(
    val id: Long,
    val name: String,
    val code: EntityCode,
    val code2: EntityCode?,
    val standard: String?,
    val dtd: String?,
    val description: String?,
) {
    /**
     * Generates the numeric HTML entity reference string in decimal format.
     *
     * Concatenates primary and optional secondary code in decimal format.
     *
     * @return The complete HTML numeric entity reference string
     * @see EntityCode.toDecimalString
     */
    fun entityReference(): String = code.toDecimalString() + (code2?.toDecimalString() ?: "")
}
