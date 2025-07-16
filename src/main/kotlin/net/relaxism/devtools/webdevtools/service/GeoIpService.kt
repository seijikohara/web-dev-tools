package net.relaxism.devtools.webdevtools.service

import net.relaxism.devtools.webdevtools.repository.api.GeoIpApiRepository
import org.springframework.stereotype.Service

@Service
class GeoIpService(
    private val geoIpApiRepository: GeoIpApiRepository,
) {
    // Expression body function with validation using scope functions
    suspend fun getGeoFromIpAddress(ipAddress: String) =
        ipAddress
            .takeIf { it.isNotBlank() }
            ?.let { validIpAddress ->
                geoIpApiRepository.getGeoByIpAddress(validIpAddress)
            } ?: throw IllegalArgumentException("IP address cannot be blank")
}
