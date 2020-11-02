package net.relaxism.devtools.webdevtools.service

import net.relaxism.devtools.webdevtools.utils.JsonUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI

@Service
class ExternalJsonApiService(@Autowired val webClient: WebClient) {

    fun get(uri: URI): Mono<Map<String?, Any?>> =
        webClient.get()
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .flatMap { clientResponse -> clientResponse.bodyToMono(String::class.java); }
            .map { response -> JsonUtils.fromJson(response) }


}
