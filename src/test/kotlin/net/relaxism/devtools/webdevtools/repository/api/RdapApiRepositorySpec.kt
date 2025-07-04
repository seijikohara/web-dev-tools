package net.relaxism.devtools.webdevtools.repository.api

import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class RdapApiRepositorySpec(
    private val rdapApiRepository: RdapApiRepository,
) : StringSpec() {
    init {
        "get : success" {
            runTest {
                JSONObject(rdapApiRepository.getRdapByIpAddress("1.1.1.1")).toString() shouldContainJsonKey "$.entities"
            }
        }

        "get : fail" {
            runTest {
                shouldThrow<RdapApiRepository.NotFoundRdapUriException> {
                    rdapApiRepository.getRdapByIpAddress("0.0.0.0")
                }
            }
        }
    }
}
