package net.relaxism.devtools.webdevtools.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class JsonUtilsSpec : StringSpec() {
    init {
        "JSON to Map" {
            JsonUtils.fromJson(null) shouldBe mapOf()
            JsonUtils.fromJson("{}") shouldBe mapOf()
            JsonUtils.fromJson("{ \"a\": 1 }") shouldBe mapOf("a" to 1)
        }

        "Map to JSON" {
            JsonUtils.toJson(null) shouldBe ""
            JsonUtils.toJson(mapOf<String, Any>()) shouldBe "{}"
            JsonUtils.toJson(mapOf("a" to 1)) shouldBe "{\"a\":1}"
        }
    }
}
