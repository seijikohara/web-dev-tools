package io.github.seijikohara.devtools.domain.htmlreference.model

/**
 * HTML entity domain model.
 *
 * Represents an HTML character entity with its name, numeric codes, and metadata.
 * Examples: &amp; (ampersand), &lt; (less than), &copy; (copyright symbol)
 *
 * @property id The unique identifier of the HTML entity
 * @property name The entity name (e.g., "amp", "lt", "copy")
 * @property code The primary numeric code of the entity
 * @property code2 The secondary numeric code if the entity requires two codes
 * @property standard The HTML/XML standard that defines this entity
 * @property dtd The Document Type Definition that includes this entity
 * @property description A human-readable description of the entity
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
     * Generates the HTML entity reference string.
     *
     * Combines primary and secondary entity codes in decimal format.
     * Example: For &amp; (code=38), returns "&#38;"
     * Example: For entities with two codes, returns "&#code1;&#code2;"
     *
     * @return The complete HTML entity reference string
     */
    fun entityReference(): String = code.toDecimalString() + (code2?.toDecimalString() ?: "")
}
