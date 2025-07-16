package net.relaxism.devtools.webdevtools.handler

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.relaxism.devtools.webdevtools.config.ApplicationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IndexHandlerSpec(
    private val webTestClient: WebTestClient,
    private val applicationProperties: ApplicationProperties,
) : FunSpec({

        test("getIndex should return HTML content for various paths") {
            forAll(
                row("/", "root path"),
                row("/index", "index path"),
                row("/app", "app path"),
            ) { path, description ->
                webTestClient
                    .get()
                    .uri(path)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectHeader()
                    .contentType(MediaType.TEXT_HTML)
                    .expectBody(String::class.java)
                    .value { content ->
                        content shouldNotBe null
                        content.isNotEmpty() shouldBe true
                    }
            }
        }

        test("IndexHandler should be properly configured with ApplicationProperties") {
            applicationProperties shouldNotBe null
            applicationProperties.indexFile shouldNotBe null
        }
    })
