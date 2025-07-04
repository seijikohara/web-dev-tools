package net.relaxism.devtools.webdevtools.service

import net.relaxism.devtools.webdevtools.repository.api.RdapApiRepository
import org.springframework.stereotype.Service

@Service
class RdapService(
    private val rdapApiRepository: RdapApiRepository,
) {
    suspend fun getRdapByIpAddress(ipAddress: String) = rdapApiRepository.getRdapByIpAddress(ipAddress)
}
