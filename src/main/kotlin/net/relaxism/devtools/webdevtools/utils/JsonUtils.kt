package net.relaxism.devtools.webdevtools.utils

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

object JsonUtils {
    val json = Json { ignoreUnknownKeys = true }

    inline fun <reified T> fromJson(value: String?): T? = value?.takeUnless { it.isBlank() }?.let { json.decodeFromString(it) }

    fun fromJson(value: String?): Map<String, Any?> =
        value?.takeUnless { it.isBlank() }?.let {
            json
                .decodeFromString<Map<String, JsonElement>>(it)
                .mapValues { (_, v) -> v.toPrimitive() }
        } ?: emptyMap()

    inline fun <reified T> toJson(value: T): String = json.encodeToString(value)

    private fun JsonElement.toPrimitive(): Any? =
        when (this) {
            is JsonNull -> null
            is JsonPrimitive ->
                if (isString) {
                    content
                } else {
                    content.toBooleanStrictOrNull()
                        ?: content.toIntOrNull()
                        ?: content.toLongOrNull()
                        ?: content.toDoubleOrNull()
                        ?: content
                }
            is JsonArray -> map { it.toPrimitive() }
            is JsonObject -> mapValues { (_, v) -> v.toPrimitive() }
        }
}
