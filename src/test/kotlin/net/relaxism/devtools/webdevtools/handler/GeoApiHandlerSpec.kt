package net.relaxism.devtools.webdevtools.handler

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import kotlinx.serialization.json.JsonPrimitive
import net.relaxism.devtools.webdevtools.config.ApplicationProperties
import net.relaxism.devtools.webdevtools.service.GeoIpService
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GeoApiHandlerSpec(
    private val webTestClient: WebTestClient,
    private val applicationProperties: ApplicationProperties,
    @MockkBean private val mockGeoIpService: GeoIpService,
) : FunSpec({

        test("getGeo should return JSON response for various IPs") {
            forAll(
                row("8.8.8.8", mapOf("country" to JsonPrimitive("US"), "city" to JsonPrimitive("Mountain View")), "IPv4 public DNS"),
                row("1.1.1.1", mapOf("country" to JsonPrimitive("US"), "city" to JsonPrimitive("San Francisco")), "IPv4 Cloudflare DNS"),
            ) { ip, mockResponse, description ->
                coEvery { mockGeoIpService.getGeoFromIpAddress(ip) } returns mockResponse

                webTestClient
                    .get()
                    .uri("${applicationProperties.apiBasePath}/geo/$ip")
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectHeader()
                    .contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.geo")
                    .exists()
                    .jsonPath("$.geo.country")
                    .isEqualTo((mockResponse["country"] as JsonPrimitive).content)
            }
        }

        test("Response data class should have proper structure") {
            val response = GeoApiHandler.Response(geo = mapOf("country" to JsonPrimitive("US")))
            response.geo shouldNotBe null
            response.geo!!["country"] shouldNotBe null
        }
    })
