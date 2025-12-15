---
paths: src/it/**/*.kt
---

# Integration Testing Guidelines (Kotest + Spring)

## File Location

Integration tests are located in `src/it/kotlin/`:

```
src/it/kotlin/.../infrastructure/web/HtmlReferenceIntegrationSpec.kt
```

## Test Class Naming

All integration test classes must end with `IntegrationSpec`:

```kotlin
class HtmlReferenceIntegrationSpec : FunSpec()
class IpInfoIntegrationSpec : FunSpec()
```

## Test Structure (FunSpec + Spring)

Use Kotest's FunSpec with Spring extensions:

```kotlin
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class HtmlReferenceIntegrationSpec(
    @LocalServerPort private val port: Int,
) : FunSpec() {
    override fun extensions() = listOf(SpringExtension)

    private val webTestClient by lazy {
        WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port")
            .build()
    }

    init {
        context("HTML Reference API") {
            test("GET /api/html-entities should return entities") {
                webTestClient
                    .get()
                    .uri("/api/html-entities?name=amp")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .jsonPath("$.entities").isArray
                    .jsonPath("$.totalCount").isNumber
            }
        }
    }
}
```

## WebTestClient Assertions

### Status Assertions

```kotlin
webTestClient
    .get()
    .uri("/api/endpoint")
    .exchange()
    .expectStatus().isOk           // 200
    .expectStatus().isNotFound     // 404
    .expectStatus().isBadRequest   // 400
    .expectStatus().is5xxServerError
```

### Header Assertions

```kotlin
.expectHeader().contentType(MediaType.APPLICATION_JSON)
.expectHeader().exists("X-Custom-Header")
.expectHeader().valueEquals("X-Custom-Header", "expected-value")
```

### JSON Body Assertions

```kotlin
.expectBody()
.jsonPath("$.field").isEqualTo("value")
.jsonPath("$.array").isArray
.jsonPath("$.array.length()").isEqualTo(3)
.jsonPath("$.nested.field").exists()
.jsonPath("$.number").isNumber
.jsonPath("$.optional").doesNotExist()
```

### Response Body Extraction

```kotlin
import kotlinx.serialization.json.Json

val response = webTestClient
    .get()
    .uri("/api/endpoint")
    .exchange()
    .expectStatus().isOk
    .expectBody<String>()
    .returnResult()
    .responseBody

val dto = Json.decodeFromString<ResponseDto>(response!!)
dto.field shouldBe expectedValue
```

## Test Configuration

### Test Profile

Use `@ActiveProfiles("test")` and create `application-test.yaml`:

```yaml
# src/it/resources/application-test.yaml
application:
  api-base-path: /api
```

### Mock External Services

For external API tests, use WireMock or similar:

```kotlin
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*

class ExternalApiIntegrationSpec : FunSpec() {
    private val wireMockServer = WireMockServer(8089)

    override suspend fun beforeSpec(spec: Spec) {
        wireMockServer.start()
    }

    override suspend fun afterSpec(spec: Spec) {
        wireMockServer.stop()
    }

    init {
        test("should handle external API response") {
            wireMockServer.stubFor(
                get(urlEqualTo("/external/endpoint"))
                    .willReturn(
                        aResponse()
                            .withStatus(200)
                            .withBody("""{"result": "success"}""")
                    )
            )
            // Test implementation
        }
    }
}
```

## Test Organization

### Context Grouping

Group tests by endpoint or feature:

```kotlin
init {
    context("GET /api/html-entities") {
        test("should return entities when name matches") { ... }
        test("should return empty list when no match") { ... }
        test("should handle pagination") { ... }
    }

    context("GET /api/html-entities with invalid parameters") {
        test("should return 400 for negative page") { ... }
        test("should return 400 for invalid size") { ... }
    }
}
```

### Database Setup

For database integration tests, use `@Sql` or programmatic setup:

```kotlin
@SpringBootTest
class DatabaseIntegrationSpec(
    private val repository: SomeRepository,
) : FunSpec() {
    override fun extensions() = listOf(SpringExtension)

    init {
        beforeEach {
            // Setup test data
        }

        afterEach {
            // Cleanup
        }
    }
}
```

## Running Integration Tests

```bash
./gradlew integrationTest                    # Run all integration tests
./gradlew integrationTest --tests "*HtmlReference*"  # Run specific tests
./gradlew check                              # Run all tests including integration
```

## Important Notes

- Integration tests require frontend to be built first
- `./gradlew integrationTest` automatically runs `npmRunBuild`
- Use `@ActiveProfiles("test")` to isolate test configuration
- Clean up test data after each test to ensure isolation
