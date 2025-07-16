package net.relaxism.devtools.webdevtools.service

import kotlinx.serialization.json.JsonElement
import net.relaxism.devtools.webdevtools.repository.api.RdapApiRepository
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
