package io.github.seijikohara.devtools.domain.networkinfo.model

import inet.ipaddr.IPAddressString

/**
 * Represents an IP address supporting both IPv4 and IPv6 formats.
 *
 * Wraps a validated IP address string using the `inet.ipaddr` library for validation.
 *
 * @property value The validated string representation of the IP address
 */
class IpAddress private constructor(
    val value: String,
) {
    /**
     * Cached parsed IP address for efficient reuse.
     * Parsing is performed only once during construction.
     */
    private val parsedAddress: inet.ipaddr.IPAddress by lazy {
        requireNotNull(IPAddressString(value).toAddress()) {
            "Invalid IP address: $value (should have been validated in of())"
        }
    }

    companion object {
        /**
         * Creates an [IpAddress] from a string value with validation.
         *
         * Validates that the input is not blank and conforms to IPv4 or IPv6 format.
         *
         * @param value The string representation of an IP address
         * @return [Result.success] containing the [IpAddress] if valid,
         *         or [Result.failure] with [IllegalArgumentException] if invalid
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
     * Converts this [IpAddress] to `inet.ipaddr.IPAddress` for library interoperability.
     *
     * @return The `inet.ipaddr.IPAddress` representation
     * @throws IllegalStateException if the IP address is invalid (should never occur due to validation in [of])
     */
    fun toInetIPAddress(): inet.ipaddr.IPAddress = parsedAddress

    /**
     * Checks if this IP address is in IPv4 format.
     *
     * @return `true` if IPv4, `false` if IPv6
     */
    fun isIpV4(): Boolean = parsedAddress.isIPv4

    /**
     * Checks if this IP address is in IPv6 format.
     *
     * @return `true` if IPv6, `false` if IPv4
     */
    fun isIpV6(): Boolean = parsedAddress.isIPv6

    /**
     * Returns the string representation of this IP address.
     *
     * @return The IP address string
     */
    override fun toString(): String = value

    override fun equals(other: Any?): Boolean = this === other || (other is IpAddress && value == other.value)

    override fun hashCode(): Int = value.hashCode()
}
