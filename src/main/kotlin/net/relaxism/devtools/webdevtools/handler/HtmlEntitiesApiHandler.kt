package net.relaxism.devtools.webdevtools.handler

import net.relaxism.devtools.webdevtools.service.HtmlEntityService
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class HtmlEntitiesApiHandler(
    private val htmlEntityService: HtmlEntityService,
) {
    suspend fun getHtmlEntities(request: ServerRequest): ServerResponse {
        val name = request.queryParam("name").orElse("")

        // Use scope functions for cleaner parameter extraction
        val pageable =
            PageRequest.of(
                request.queryParam("page").map(String::toInt).orElse(0),
                request.queryParam("size").map(String::toInt).orElse(50),
                Sort.by(Sort.Order.asc("id")),
            )

        // Use destructuring and functional approach
        return htmlEntityService
            .findByNameContaining(name, pageable)
            .let { pageHtmlEntities ->
                ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValueAndAwait(
                        PageImpl(
                            pageHtmlEntities.content.map { entity ->
                                Entity(
                                    entity.name,
                                    entity.code,
                                    entity.code2,
                                    entity.standard,
                                    entity.dtd,
                                    entity.description,
                                )
                            },
                            pageHtmlEntities.pageable,
                            pageHtmlEntities.totalElements,
                        ),
                    )
            }
    }

    data class Entity(
        val name: String,
        val code: Long,
        val code2: Long?,
        val standard: String?,
        val dtd: String?,
        val description: String?,
    ) {
        // Use Elvis operator for cleaner expression
        val entityReference: String
            get() = "&#$code;" + (code2?.let { "&#$it;" } ?: "")
    }
}
