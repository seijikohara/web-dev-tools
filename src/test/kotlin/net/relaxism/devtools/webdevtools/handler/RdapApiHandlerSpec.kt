package net.relaxism.devtools.webdevtools.handler

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import kotlinx.serialization.json.JsonPrimitive
import net.relaxism.devtools.webdevtools.config.ApplicationProperties
import net.relaxism.devtools.webdevtools.repository.api.RdapApiRepository
import net.relaxism.devtools.webdevtools.service.RdapService
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RdapApiHandlerSpec(
    private val webTestClient: WebTestClient,
    private val applicationProperties: ApplicationProperties,
    @MockkBean private val mockRdapService: RdapService,
) : FunSpec({

        test("getRdap should handle various IP addresses") {
            forAll(
                row("8.8.8.8", mapOf("handle" to JsonPrimitive("8.8.8.8"), "country" to JsonPrimitive("US")), "public IPv4"),
                row("1.1.1.1", mapOf("handle" to JsonPrimitive("1.1.1.1"), "country" to JsonPrimitive("US")), "Cloudflare DNS IPv4"),
            ) { ip, mockResponse, description ->
                coEvery { mockRdapService.getRdapByIpAddress(ip) } returns mockResponse

                webTestClient
                    .get()
                    .uri("${applicationProperties.apiBasePath}/rdap/$ip")
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectHeader()
                    .contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.rdap")
                    .exists()
                    .jsonPath("$.rdap.handle")
                    .isEqualTo((mockResponse["handle"] as JsonPrimitive).content)
            }
        }

        test("getRdap should return 404 for unsupported IP ranges") {
            forAll(
                row("192.168.1.1", "private IPv4"),
                row("10.0.0.1", "private IPv4 class A"),
                row("172.16.0.1", "private IPv4 class B"),
            ) { ip, description ->
                coEvery { mockRdapService.getRdapByIpAddress(ip) } throws RdapApiRepository.NotFoundRdapUriException("Not found")

                webTestClient
                    .get()
                    .uri("${applicationProperties.apiBasePath}/rdap/$ip")
                    .exchange()
                    .expectStatus()
                    .isNotFound()
            }
        }

        test("Response data class should have proper structure") {
            val response = RdapApiHandler.Response(rdap = mapOf("handle" to JsonPrimitive("8.8.8.8")))
            response.rdap shouldNotBe null
            response.rdap!!["handle"] shouldBe JsonPrimitive("8.8.8.8")
        }
    })
