package com.github.seijikohara.devtools.webdevtools.service

import com.github.seijikohara.devtools.webdevtools.repository.api.RdapApiRepository
import kotlinx.serialization.json.JsonElement
import org.springframework.stereotype.Service

@Service
class RdapService(
    private val rdapApiRepository: RdapApiRepository,
) {
    // Expression body function with validation using scope functions and Elvis operator
    suspend fun getRdapByIpAddress(ipAddress: String): Map<String, JsonElement> =
        ipAddress
            .takeIf { it.isNotBlank() }
            ?.run { rdapApiRepository.getRdapByIpAddress(this) }
            ?: throw IllegalArgumentException("IP address cannot be blank")
}
