package io.github.seijikohara.devtools.infrastructure.web.dto

import io.github.seijikohara.devtools.domain.networkinfo.model.GeoLocation
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapInformation

/**
 * Type alias for RDAP response DTO.
 *
 * Returns the complete RFC 9083 RDAP information directly.
 *
 * @see RdapInformation
 * @see <a href="https://datatracker.ietf.org/doc/rfc9083/">RFC 9083</a>
 */
typealias RdapResponseDto = RdapInformation

/**
 * Type alias for GeoIP response DTO.
 *
 * Returns the complete geolocation data directly.
 *
 * @see GeoLocation
 * @see <a href="https://ipapi.co/api/">ipapi.co API Reference</a>
 */
typealias GeoResponseDto = GeoLocation
