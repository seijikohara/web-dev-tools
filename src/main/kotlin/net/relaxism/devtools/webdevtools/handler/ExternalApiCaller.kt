package net.relaxism.devtools.webdevtools.handler

import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate

interface ExternalApiCaller {

    fun callExternalApi(restTemplate: RestTemplate, uri: String): String {
        return runCatching {
            restTemplate.getForEntity(uri, String::class.java)
        }.fold(
            onSuccess = { it.body ?: "{}" },
            onFailure = {
                when (it) {
                    is RestClientResponseException -> {
                        return it.responseBodyAsString
                    }
                    else -> throw it
                }
            }
        )
    }

}
