package net.relaxism.devtools.webdevtools.service

import io.kotest.core.spec.style.StringSpec
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier
import java.net.URI

class ExternalJsonApiServiceSpec() : StringSpec() {

    init {
        val mockWebServer = MockWebServer()
        val testUri = URI.create(mockWebServer.url("/test").toString())
        val externalJsonApiService = ExternalJsonApiService(WebClient.create(testUri.toString()))

        "get : success" {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(HttpStatus.OK.value())
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody("{\"key1\": 1,\"key2\": \"value2\"}")
            )

            StepVerifier
                .create(externalJsonApiService.get(testUri))
                .expectNext(mapOf("key1" to 1, "key2" to "value2"))
                .verifyComplete();
        }

        "get : success(No response body)" {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(HttpStatus.NO_CONTENT.value())
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            )

            StepVerifier
                .create(externalJsonApiService.get(testUri))
                .expectNext(mapOf())
                .verifyComplete();
        }

        "get : failed(RestClientResponseException)" {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(HttpStatus.BAD_REQUEST.value())
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody("{\"message\": \"error\"}")
            )

            StepVerifier
                .create(externalJsonApiService.get(testUri))
                .expectNext(mapOf("message" to "error"))
                .verifyComplete();
        }

        "get : failed(RestClientResponseException, No response body)" {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            )

            StepVerifier
                .create(externalJsonApiService.get(testUri))
                .expectNext(mapOf())
                .verifyComplete();
        }

    }

}
