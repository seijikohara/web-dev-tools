@file:Suppress("ktlint:standard:no-consecutive-comments")

package io.github.seijikohara.devtools.infrastructure.externalapi.rdap

import io.github.seijikohara.devtools.domain.networkinfo.model.CountryCode
import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapInformation
import io.github.seijikohara.devtools.infrastructure.externalapi.common.decodeJsonToElements
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

/**
 * RDAP API response data.
 *
 * @property data JSON response data
 */
@ConsistentCopyVisibility
data class RdapResponse internal constructor(
    val data: Map<String, JsonElement>,
)

/**
 * Decodes JSON string to [RdapResponse].
 *
 * @param json JSON string to decode
 * @return [RdapResponse] instance
 */
fun decodeJsonToRdapResponse(json: String?): RdapResponse = RdapResponse(decodeJsonToElements(json))

/**
 * Converts to [RdapInformation].
 *
 * @receiver Response data
 * @param ipAddress IP address
 * @return [RdapInformation] instance
 */
fun RdapResponse.toDomain(ipAddress: IpAddress): RdapInformation =
    RdapInformation(
        ipAddress = ipAddress,
        handle = data["handle"]?.jsonPrimitive?.content,
        name = data["name"]?.jsonPrimitive?.content,
        country =
            data["country"]
                ?.jsonPrimitive
                ?.content
                ?.let(CountryCode::of)
                ?.getOrNull(),
        registeredAt = null, // TODO: Parse from events array if needed
    )
