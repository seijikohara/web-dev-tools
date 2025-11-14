package io.github.seijikohara.devtools.application.usecase

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class GetHttpHeadersUseCaseSpec :
    FunSpec({

        context("GetHttpHeadersUseCase execution") {
            test("should return empty list for empty headers map") {
                val useCase = getHttpHeadersUseCase()
                val request = GetHttpHeadersUseCase.Request(headers = emptyMap())

                val response = useCase(request)

                response.headers.shouldBeEmpty()
            }

            test("should return single header for single-value header") {
                val useCase = getHttpHeadersUseCase()
                val request =
                    GetHttpHeadersUseCase.Request(
                        headers = mapOf("Content-Type" to listOf("application/json")),
                    )

                val response = useCase(request)

                response.headers shouldHaveSize 1
                response.headers[0].name shouldBe "Content-Type"
                response.headers[0].value shouldBe "application/json"
            }

            test("should flatten multi-value headers into individual name-value pairs") {
                val useCase = getHttpHeadersUseCase()
                val request =
                    GetHttpHeadersUseCase.Request(
                        headers =
                            mapOf(
                                "Accept" to listOf("text/html", "application/json", "application/xml"),
                            ),
                    )

                val response = useCase(request)

                response.headers shouldHaveSize 3
                response.headers[0].name shouldBe "Accept"
                response.headers[0].value shouldBe "text/html"
                response.headers[1].name shouldBe "Accept"
                response.headers[1].value shouldBe "application/json"
                response.headers[2].name shouldBe "Accept"
                response.headers[2].value shouldBe "application/xml"
            }

            test("should handle multiple headers with mixed single and multi-value") {
                val useCase = getHttpHeadersUseCase()
                val request =
                    GetHttpHeadersUseCase.Request(
                        headers =
                            mapOf(
                                "Content-Type" to listOf("application/json"),
                                "Accept" to listOf("text/html", "application/json"),
                                "User-Agent" to listOf("Mozilla/5.0"),
                            ),
                    )

                val response = useCase(request)

                response.headers shouldHaveSize 4
                // Note: Map iteration order is not guaranteed, but we can check by name
                val contentTypeHeaders = response.headers.filter { it.name == "Content-Type" }
                contentTypeHeaders shouldHaveSize 1
                contentTypeHeaders[0].value shouldBe "application/json"

                val acceptHeaders = response.headers.filter { it.name == "Accept" }
                acceptHeaders shouldHaveSize 2
                acceptHeaders.map { it.value } shouldBe listOf("text/html", "application/json")

                val userAgentHeaders = response.headers.filter { it.name == "User-Agent" }
                userAgentHeaders shouldHaveSize 1
                userAgentHeaders[0].value shouldBe "Mozilla/5.0"
            }

            test("should return empty list for header with empty value list") {
                val useCase = getHttpHeadersUseCase()
                val request =
                    GetHttpHeadersUseCase.Request(
                        headers = mapOf("X-Custom-Header" to emptyList()),
                    )

                val response = useCase(request)

                response.headers.shouldBeEmpty()
            }

            data class SpecialCharacterTestCase(
                val description: String,
                val headerName: String,
                val headerValue: String,
            )

            withData(
                nameFn = { it.description },
                SpecialCharacterTestCase(
                    description = "header with whitespace in value",
                    headerName = "X-Custom",
                    headerValue = "value with spaces",
                ),
                SpecialCharacterTestCase(
                    description = "header with special characters",
                    headerName = "X-Custom",
                    headerValue = "value; charset=utf-8",
                ),
                SpecialCharacterTestCase(
                    description = "header with hyphenated name",
                    headerName = "X-Forwarded-For",
                    headerValue = "192.168.1.1",
                ),
            ) { (_, headerName, headerValue) ->
                val useCase = getHttpHeadersUseCase()
                val request =
                    GetHttpHeadersUseCase.Request(
                        headers = mapOf(headerName to listOf(headerValue)),
                    )

                val response = useCase(request)

                response.headers shouldHaveSize 1
                response.headers[0].name shouldBe headerName
                response.headers[0].value shouldBe headerValue
            }
        }
    })
