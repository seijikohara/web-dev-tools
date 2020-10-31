package net.relaxism.devtools.webdevtools.service

import net.relaxism.devtools.webdevtools.utils.JsonUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import java.net.URI

@Service
class ExternalJsonApiService(@Autowired val restTemplate: RestTemplate) {

    fun get(uri: String): Map<String?, Any?> {
        return get(URI.create(uri))
    }

    fun get(uri: URI): Map<String?, Any?> {
        return runCatching {
            restTemplate.getForEntity(uri, String::class.java)
        }.fold(
            onSuccess = { JsonUtils.fromJson(it.body ?: "{}") },
            onFailure = {
                when (it) {
                    is RestClientResponseException -> {
                        return JsonUtils.fromJson(it.responseBodyAsString)
                    }
                    else -> throw it
                }
            }
        )
    }

}
