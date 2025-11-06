package io.github.seijikohara.devtools.domain.networkinfo.model

import inet.ipaddr.IPAddressString

/**
 * IP address value object supporting both IPv4 and IPv6.
 *
 * This value class provides type-safe IP address handling with validation.
 * Internally uses the IPAddress library (com.github.seancfoley:ipaddress) for robust validation and parsing.
 *
 * @property value The string representation of the IP address
 */
@JvmInline
value class IpAddress private constructor(
    val value: String,
) {
    companion object {
        /**
         * Creates an IpAddress from a string value.
         *
         * @param value The string representation of an IP address
         * @return Result containing the IpAddress if valid, or a failure with an exception
         */
        fun of(value: String): Result<IpAddress> =
            runCatching {
                require(value.isNotBlank()) { "IP address cannot be blank" }
                // Validate using IPAddress library - convert any exception to IllegalArgumentException
                try {
                    require(IPAddressString(value).toAddress() != null) {
                        "Invalid IP address format: $value"
                    }
                } catch (e: Exception) {
                    throw IllegalArgumentException("Invalid IP address format: $value", e)
                }
                IpAddress(value)
            }
    }

    /**
     * Converts this IpAddress to inet.ipaddr.IPAddress for interoperability with existing code.
     *
     * @throws IllegalStateException if the IP address is invalid (should never occur due to validation in of())
     */
    fun toInetIPAddress(): inet.ipaddr.IPAddress =
        requireNotNull(IPAddressString(value).toAddress()) {
            "Invalid IP address: $value (should have been validated in of())"
        }

    /**
     * Checks if this IP address is IPv4.
     */
    fun isIpV4(): Boolean = toInetIPAddress().isIPv4

    /**
     * Checks if this IP address is IPv6.
     */
    fun isIpV6(): Boolean = toInetIPAddress().isIPv6

    override fun toString(): String = value
}
