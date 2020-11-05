package net.relaxism.devtools.webdevtools.handler

import net.relaxism.devtools.webdevtools.service.HtmlEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class HtmlEntitiesApiHandler(@Autowired private val htmlEntityService: HtmlEntityService) {

    fun getHtmlEntities(request: ServerRequest): Mono<ServerResponse> {
        val entities = htmlEntityService.findAll()
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                entities.map { Response.Entity(it.name, it.code, it.code2, it.standard, it.dtd, it.description) }
                    .collectList()
                    .map { Response(it) },
                Response::class.java)
    }

    data class Response(val entities: List<Entity>) {
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

}
