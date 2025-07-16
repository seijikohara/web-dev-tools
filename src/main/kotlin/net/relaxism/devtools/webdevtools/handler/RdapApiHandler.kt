package net.relaxism.devtools.webdevtools.handler

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import net.relaxism.devtools.webdevtools.repository.api.RdapApiRepository
import net.relaxism.devtools.webdevtools.service.RdapService
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait

@Component
class RdapApiHandler(
    private val rdapService: RdapService,
) {
    suspend fun getRdap(request: ServerRequest): ServerResponse =
        request
            .pathVariable("ip")
            .let { ipAddress ->
                runCatching { rdapService.getRdapByIpAddress(ipAddress) }
                    .fold(
                        onSuccess = { rdapData ->
                            ServerResponse
                                .ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValueAndAwait(Response(rdap = rdapData))
                        },
                        onFailure = { exception ->
                            when (exception) {
                                is RdapApiRepository.NotFoundRdapUriException ->
                                    ServerResponse.notFound().buildAndAwait()
                                else -> throw exception
                            }
                        },
                    )
            }

    @Serializable
    data class Response(
        val rdap: Map<String, JsonElement>?,
    )
}
