package io.github.seijikohara.devtools.infrastructure.database.htmlreference

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

/**
 * Database entity for HTML entities.
 *
 * @property id Primary key
 * @property name Entity name
 * @property code Primary Unicode code point
 * @property code2 Secondary Unicode code point for entities requiring two codes
 * @property standard HTML standard specification
 * @property dtd Document Type Definition
 * @property description Human-readable description
 */
@Table("HTML_ENTITY")
data class HtmlEntityDbEntity(
    @Id @Column val id: Long? = null,
    @Column val name: String,
    @Column val code: Long,
    @Column val code2: Long? = null,
    @Column val standard: String? = null,
    @Column val dtd: String? = null,
    @Column val description: String? = null,
)
