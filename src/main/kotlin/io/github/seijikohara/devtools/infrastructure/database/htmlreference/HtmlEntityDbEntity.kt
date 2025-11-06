package io.github.seijikohara.devtools.infrastructure.database.htmlreference

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

/**
 * Database entity for HTML entities.
 *
 * This is the persistence model used by Spring Data R2DBC.
 * Separate from the domain model to maintain clean architecture boundaries.
 *
 * @property id Primary key (auto-generated)
 * @property name Entity name (e.g., "nbsp", "lt", "gt")
 * @property code Primary character code (decimal Unicode code point)
 * @property code2 Secondary character code for entities with two code points
 * @property standard HTML standard specification (e.g., "HTML 5.0", "HTML 4.0")
 * @property dtd Document Type Definition (e.g., "HTML 4.01", "XHTML 1.0")
 * @property description Human-readable description of the entity
 */
@Table("html_entity")
data class HtmlEntityDbEntity(
    @Id @Column val id: Long? = null,
    @Column val name: String,
    @Column val code: Long,
    @Column val code2: Long? = null,
    @Column val standard: String? = null,
    @Column val dtd: String? = null,
    @Column val description: String? = null,
)
