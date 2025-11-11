package io.github.seijikohara.devtools.infrastructure.database.htmlreference

import io.github.seijikohara.devtools.domain.common.extensions.flatMap
import io.github.seijikohara.devtools.domain.htmlreference.model.EntityCode
import io.github.seijikohara.devtools.domain.htmlreference.model.HtmlEntity

/**
 * Converts database entity to domain entity.
 *
 * Returns a Result to handle potential validation errors in a pure functional way.
 * Uses Result type composition with expression chains to avoid intermediate variables
 * and early returns, achieving complete functional purity.
 *
 * The conversion process validates:
 * - Entity ID must not be null
 * - Primary entity code must be valid
 * - Secondary entity code (if present) must be valid
 *
 * @receiver Database entity to convert
 * @return Result containing HtmlEntity if successful, or error if validation fails
 */
fun HtmlEntityDbEntity.toDomain(): Result<HtmlEntity> =
    runCatching { requireNotNull(id) { "HTML entity ID cannot be null" } }
        .flatMap { validId ->
            EntityCode
                .of(code)
                .flatMap { validCode ->
                    (code2?.let { EntityCode.of(it) } ?: Result.success(null))
                        .map { validCode2 ->
                            HtmlEntity(
                                id = validId,
                                name = name,
                                code = validCode,
                                code2 = validCode2,
                                standard = standard,
                                dtd = dtd,
                                description = description,
                            )
                        }
                }
        }
