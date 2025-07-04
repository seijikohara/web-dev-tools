package net.relaxism.devtools.webdevtools.service

import net.relaxism.devtools.webdevtools.component.api.GeoIpClient
import org.springframework.stereotype.Service

@Service
class GeoIpService(
    private val geoIpClient: GeoIpClient,
) {
    suspend fun getGeoFromIpAddress(ipAddress: String) = geoIpClient.getGeoByIpAddress(ipAddress)
}
