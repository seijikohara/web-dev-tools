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
        value
            ?.takeUnless(String::isBlank)
            ?.let { jsonString ->
                json
                    .decodeFromString<Map<String, JsonElement>>(jsonString)
                    .mapValues { (_, jsonElement) ->
                        fun convertElement(element: JsonElement): Any? =
                            when (element) {
                                is JsonNull -> null
                                is JsonPrimitive ->
                                    if (element.isString) {
                                        element.content
                                    } else {
                                        listOf(
                                            String::toBooleanStrictOrNull,
                                            String::toIntOrNull,
                                            String::toLongOrNull,
                                            String::toDoubleOrNull,
                                        ).asSequence()
                                            .mapNotNull { converter -> converter(element.content) }
                                            .firstOrNull()
                                            ?: element.content
                                    }
                                is JsonArray -> element.map(::convertElement)
                                is JsonObject -> element.mapValues { (_, nestedElement) -> convertElement(nestedElement) }
                            }
                        convertElement(jsonElement)
                    }
            } ?: emptyMap()

    inline fun <reified T> toJson(value: T): String = json.encodeToString(value)

    fun fromJsonToElements(value: String?): Map<String, JsonElement> =
        value
            ?.takeUnless(String::isBlank)
            ?.let { jsonString ->
                json.decodeFromString<Map<String, JsonElement>>(jsonString)
            } ?: emptyMap()
}
