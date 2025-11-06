package io.github.seijikohara.devtools.infrastructure.web

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

/**
 * Simple integration test to verify basic Spring Boot test setup.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@ApplyExtension(SpringExtension::class)
class SimpleIntegrationTest(
    private val webTestClient: WebTestClient,
) : FunSpec({
    test("Spring context should load") {
        webTestClient
            .get()
            .uri("/api/http-headers")
            .exchange()
            .expectStatus().isOk
    }
})
