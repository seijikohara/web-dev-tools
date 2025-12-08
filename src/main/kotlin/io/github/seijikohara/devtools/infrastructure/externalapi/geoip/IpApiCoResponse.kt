@file:Suppress("ktlint:standard:max-line-length")

package io.github.seijikohara.devtools.infrastructure.externalapi.geoip

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Complete response from ipapi.co API.
 *
 * This data class represents the full JSON response from the ipapi.co geolocation API.
 * All fields are nullable to handle partial responses or API variations.
 *
 * @see <a href="https://ipapi.co/api/">ipapi.co API Reference</a>
 *
 * @property ip The IP address that was looked up
 * @property version IP version ("IPv4" or "IPv6")
 * @property city City name
 * @property region Region/state name (e.g., "California")
 * @property regionCode Region/state code (e.g., "CA")
 * @property country Two-letter ISO 3166-1 alpha-2 country code (e.g., "US"). Alias for countryCode
 * @property countryCode Two-letter ISO 3166-1 alpha-2 country code (e.g., "US")
 * @property countryCodeIso3 Three-letter ISO 3166-1 alpha-3 country code (e.g., "USA")
 * @property countryName Full country name (e.g., "United States")
 * @property countryCapital Capital city of the country (e.g., "Washington")
 * @property countryTld Country's top-level domain (e.g., ".us")
 * @property continentCode Two-letter continent code (e.g., "NA" for North America)
 * @property inEu Whether the country is in the European Union
 * @property postal Postal/ZIP code
 * @property latitude Latitude coordinate (WGS84)
 * @property longitude Longitude coordinate (WGS84)
 * @property timezone IANA timezone identifier (e.g., "America/Los_Angeles")
 * @property utcOffset UTC offset in format "+HHMM" or "-HHMM" (e.g., "-0800")
 * @property countryCallingCode Country calling code with "+" prefix (e.g., "+1")
 * @property currency Three-letter ISO 4217 currency code (e.g., "USD")
 * @property currencyName Full currency name (e.g., "Dollar")
 * @property languages Comma-separated list of language codes (e.g., "en-US,es-US,haw,fr")
 * @property countryArea Country area in square kilometers
 * @property countryPopulation Country population count
 * @property asn Autonomous System Number with "AS" prefix (e.g., "AS15169")
 * @property org Organization name associated with the ASN (e.g., "Google LLC")
 * @property hostname Reverse DNS hostname (optional add-on, may be null)
 * @property network Network CIDR block (e.g., "8.8.8.0/24")
 * @property error Boolean indicating if an error occurred (present only on error responses)
 * @property reason Error reason message (present only on error responses)
 * @property reserved Boolean indicating if the IP is a reserved/private address
 * @property message Additional message (sometimes present in error responses)
 */
@Serializable
data class IpApiCoResponse(
    val ip: String? = null,
    val version: String? = null,
    val city: String? = null,
    val region: String? = null,
    @SerialName("region_code")
    val regionCode: String? = null,
    val country: String? = null,
    @SerialName("country_code")
    val countryCode: String? = null,
    @SerialName("country_code_iso3")
    val countryCodeIso3: String? = null,
    @SerialName("country_name")
    val countryName: String? = null,
    @SerialName("country_capital")
    val countryCapital: String? = null,
    @SerialName("country_tld")
    val countryTld: String? = null,
    @SerialName("continent_code")
    val continentCode: String? = null,
    @SerialName("in_eu")
    val inEu: Boolean? = null,
    val postal: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timezone: String? = null,
    @SerialName("utc_offset")
    val utcOffset: String? = null,
    @SerialName("country_calling_code")
    val countryCallingCode: String? = null,
    val currency: String? = null,
    @SerialName("currency_name")
    val currencyName: String? = null,
    val languages: String? = null,
    @SerialName("country_area")
    val countryArea: Double? = null,
    @SerialName("country_population")
    val countryPopulation: Long? = null,
    val asn: String? = null,
    val org: String? = null,
    val hostname: String? = null,
    val network: String? = null,
    val error: Boolean? = null,
    val reason: String? = null,
    val reserved: Boolean? = null,
    val message: String? = null,
)
