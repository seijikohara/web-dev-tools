package net.relaxism.devtools.webdevtools.handler

import net.relaxism.devtools.webdevtools.service.HtmlEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class HtmlEntitiesApiHandler(@Autowired private val htmlEntityService: HtmlEntityService) {

    suspend fun getHtmlEntities(request: ServerRequest): ServerResponse {
        val name = request.queryParam("name").orElse("")
        val pageable: Pageable = PageRequest.of(
            request.queryParam("page").map { Integer.parseInt(it) }.orElse(0),
            request.queryParam("size").map { Integer.parseInt(it) }.orElse(50),
            Sort.by(Sort.Order.asc("id"))
        )
        val pageHtmlEntities = htmlEntityService.findByNameContaining(name, pageable)

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                PageImpl(
                    pageHtmlEntities.content.map {
                        Entity(
                            it.name,
                            it.code,
                            it.code2,
                            it.standard,
                            it.dtd,
                            it.description
                        )
                    },
                    pageHtmlEntities.pageable,
                    pageHtmlEntities.totalElements
                )
            )
    }

    data class Entity(
        val name: String,
        val code: Long,
        val code2: Long?,
        val standard: String?,
        val dtd: String?,
        val description: String?,
    ) {
        val entityReference: String
            get() = "&#${code};" + if (code2 != null) "&#${code2};" else ""
    }

}
