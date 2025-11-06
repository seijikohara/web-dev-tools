package io.github.seijikohara.devtools.infrastructure.externalapi.rdap

import io.github.seijikohara.devtools.domain.networkinfo.model.CountryCode
import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapInformation
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

/**
 * Converts RDAP API response to domain model.
 *
 * Transforms JSON responses from RDAP servers into domain objects.
 *
 * @receiver The raw JSON response as a map
 * @param ipAddress The IP address being queried
 * @return RdapInformation domain object
 */
fun Map<String, JsonElement>.toRdapInformation(ipAddress: IpAddress): RdapInformation =
    RdapInformation(
        ipAddress = ipAddress,
        handle = this["handle"]?.jsonPrimitive?.content,
        name = this["name"]?.jsonPrimitive?.content,
        country =
            this["country"]
                ?.jsonPrimitive
                ?.content
                ?.let(CountryCode::of)
                ?.getOrNull(),
        registeredAt = null, // TODO: Parse from events array if needed
        rawData = this,
    )
