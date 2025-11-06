package io.github.seijikohara.devtools.infrastructure.externalapi.common

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * JSON configuration for external API communication.
 *
 * Configured to ignore unknown keys to handle various external API responses.
 */
val json = Json { ignoreUnknownKeys = true }

/**
 * Decodes a JSON string into a typed object.
 *
 * @param T The target type to decode into
 * @param value The JSON string to decode
 * @return The decoded object, or null if the string is null or blank
 */
inline fun <reified T> decodeJson(value: String?): T? = value?.takeUnless { it.isBlank() }?.let { json.decodeFromString(it) }

/**
 * Decodes a JSON string into a map of JsonElements.
 *
 * This function is useful for parsing dynamic API responses where the structure
 * may vary or when you need to access raw JSON elements.
 *
 * @param value The JSON string to decode
 * @return A map of field names to JsonElements, or an empty map if the string is null or blank
 */
fun decodeJsonToElements(value: String?): Map<String, JsonElement> =
    value
        ?.takeUnless(String::isBlank)
        ?.let { json.decodeFromString<Map<String, JsonElement>>(it) }
        ?: emptyMap()
