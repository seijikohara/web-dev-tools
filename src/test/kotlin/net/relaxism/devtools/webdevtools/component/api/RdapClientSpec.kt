package net.relaxism.devtools.webdevtools.component.api

import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class RdapClientSpec(
    @Autowired private val rdapClient: RdapClient,
) : StringSpec() {
    init {
        "get : success" {
            runTest {
                JSONObject(rdapClient.getRdapByIpAddress("1.1.1.1")).toString() shouldContainJsonKey "$.entities"
            }
        }

        "get : fail" {
            runTest {
                shouldThrow<RdapClient.NotFoundRdapUriException> {
                    rdapClient.getRdapByIpAddress("0.0.0.0")
                }
            }
        }
    }
}
