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
    suspend fun getHtmlEntities(request: ServerRequest): ServerResponse =
        Pair(
            request.queryParam("name").orElse(""),
            PageRequest.of(
                request.queryParam("page").map(String::toInt).orElse(0),
                request.queryParam("size").map(String::toInt).orElse(50),
                Sort.by(Sort.Order.asc("id")),
            ),
        ).let { (name, pageable) ->
            htmlEntityService.findByNameContaining(name, pageable)
        }.let { pageHtmlEntities ->
            PageImpl(
                pageHtmlEntities.content.map { entity ->
                    Entity(
                        name = entity.name,
                        code = entity.code,
                        code2 = entity.code2,
                        standard = entity.standard,
                        dtd = entity.dtd,
                        description = entity.description,
                    )
                },
                pageHtmlEntities.pageable,
                pageHtmlEntities.totalElements,
            )
        }.let { responsePage ->
            ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(responsePage)
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
