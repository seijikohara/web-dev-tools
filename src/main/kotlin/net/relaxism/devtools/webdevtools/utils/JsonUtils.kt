package net.relaxism.devtools.webdevtools.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

object JsonUtils {

    private val objectMapper = ObjectMapper()

    fun fromJson(value: String?): Map<String?, Any?> {
        return objectMapper.readValue(value ?: "{}", object : TypeReference<Map<String?, Any?>>() {})
    }

    fun toJson(value: Any?): String {
        if (value == null) {
            return "";
        }
        return objectMapper.writeValueAsString(value)
    }

}
