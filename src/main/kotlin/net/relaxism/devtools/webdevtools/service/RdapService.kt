package net.relaxism.devtools.webdevtools.service

import net.relaxism.devtools.webdevtools.component.api.RdapClient
import org.springframework.stereotype.Service

@Service
class RdapService(
    private val rdapClient: RdapClient,
) {
    suspend fun getRdapByIpAddress(ipAddress: String) = rdapClient.getRdapByIpAddress(ipAddress)
}
