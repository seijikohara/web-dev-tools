package net.relaxism.devtools.webdevtools.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

object JsonUtils {
    private val DEFAULT_OBJECT_MAPPER = ObjectMapper().findAndRegisterModules()

    fun <T> fromJson(
        value: String?,
        valueType: Class<T>,
    ): T? = fromJson(DEFAULT_OBJECT_MAPPER, value, valueType)

    fun <T> fromJson(
        objectMapper: ObjectMapper,
        value: String?,
        valueType: Class<T>,
    ): T? {
        if (value.isNullOrBlank()) {
            return null
        }
        return objectMapper.readValue(value, valueType)
    }

    fun fromJson(value: String?): Map<String, Any?> = fromJson(DEFAULT_OBJECT_MAPPER, value)

    fun fromJson(
        objectMapper: ObjectMapper,
        value: String?,
    ): Map<String, Any?> {
        if (value.isNullOrBlank()) {
            return mapOf()
        }
        return objectMapper.readValue(value, object : TypeReference<Map<String, Any?>>() {})
    }

    fun toJson(value: Any?): String = toJson(DEFAULT_OBJECT_MAPPER, value)

    fun toJson(
        objectMapper: ObjectMapper,
        value: Any?,
    ): String {
        if (value == null) {
            return ""
        }
        return objectMapper.writeValueAsString(value)
    }
}
