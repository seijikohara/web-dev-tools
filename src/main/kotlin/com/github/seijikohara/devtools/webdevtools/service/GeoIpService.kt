package com.github.seijikohara.devtools.webdevtools.service

import com.github.seijikohara.devtools.webdevtools.repository.api.GeoIpApiRepository
import kotlinx.serialization.json.JsonElement
import org.springframework.stereotype.Service

@Service
class GeoIpService(
    private val geoIpApiRepository: GeoIpApiRepository,
) {
    // Expression body function with validation using scope functions
    suspend fun getGeoFromIpAddress(ipAddress: String): Map<String, JsonElement>? =
        ipAddress
            .takeIf { it.isNotBlank() }
            ?.let { validIpAddress ->
                geoIpApiRepository.getGeoByIpAddress(validIpAddress)
            } ?: throw IllegalArgumentException("IP address cannot be blank")
}
