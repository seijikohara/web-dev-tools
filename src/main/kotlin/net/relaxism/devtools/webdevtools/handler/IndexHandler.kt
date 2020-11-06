package net.relaxism.devtools.webdevtools.handler

import net.relaxism.devtools.webdevtools.config.ApplicationProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class IndexHandler(
    @Autowired private val applicationProperties: ApplicationProperties
) {

    fun getIndex(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok()
            .contentType(MediaType.TEXT_HTML)
            .bodyValue(applicationProperties.indexFile)
    }

}
