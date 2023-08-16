package net.relaxism.devtools.webdevtools.handler

import net.relaxism.devtools.webdevtools.component.api.RdapClient
import net.relaxism.devtools.webdevtools.service.RdapService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait

@Component
class RdapApiHandler(
    @Autowired private val rdapService: RdapService
) {

    suspend fun getRdap(request: ServerRequest): ServerResponse {
        val ipAddress = request.pathVariable("ip")
        return try {
            val clientResponse = rdapService.getRdapByIpAddress(ipAddress)
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(Response(rdap = clientResponse))
        } catch (e: RdapClient.NotFoundRdapUriException) {
            ServerResponse.notFound()
                .buildAndAwait()
        }
    }

    data class Response(val rdap: Map<String, Any?>?)

}
