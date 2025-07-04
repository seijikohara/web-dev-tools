package net.relaxism.devtools.webdevtools.service

import net.relaxism.devtools.webdevtools.repository.api.GeoIpApiRepository
import org.springframework.stereotype.Service

@Service
class GeoIpService(
    private val geoIpApiRepository: GeoIpApiRepository,
) {
    suspend fun getGeoFromIpAddress(ipAddress: String) = geoIpApiRepository.getGeoByIpAddress(ipAddress)
}
