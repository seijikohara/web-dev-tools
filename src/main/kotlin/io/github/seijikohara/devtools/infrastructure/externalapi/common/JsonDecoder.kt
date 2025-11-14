package io.github.seijikohara.devtools.infrastructure.externalapi.common

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * JSON decoder configuration.
 */
val json = Json { ignoreUnknownKeys = true }

/**
 * Decodes JSON string.
 *
 * @param T Target type
 * @param value JSON string to decode
 * @return Decoded object, or null if input is null or blank
 */
inline fun <reified T> decodeJson(value: String?): T? = value?.takeUnless { it.isBlank() }?.let { json.decodeFromString(it) }

/**
 * Decodes JSON string to map of elements.
 *
 * @param value JSON string to decode
 * @return Map of field names to [JsonElement], or empty map if input is null or blank
 */
fun decodeJsonToElements(value: String?): Map<String, JsonElement> =
    value
        ?.takeUnless(String::isBlank)
        ?.let { json.decodeFromString<Map<String, JsonElement>>(it) }
        ?: emptyMap()
