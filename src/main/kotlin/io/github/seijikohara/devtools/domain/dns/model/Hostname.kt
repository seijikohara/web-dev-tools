package io.github.seijikohara.devtools.domain.dns.model

/**
 * Represents a valid hostname for DNS resolution.
 *
 * Validates that the hostname follows RFC 1123 hostname requirements.
 *
 * @property value The hostname string
 */
@JvmInline
value class Hostname private constructor(
    val value: String,
) {
    companion object {
        private val HOSTNAME_PATTERN =
            Regex(
                "^(?=.{1,253}\$)(?:(?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)*(?!-)[A-Za-z0-9-]{1,63}(?<!-)\$",
            )

        /**
         * Creates a [Hostname] from a string.
         *
         * @param value The hostname string
         * @return [Result] containing [Hostname] on success, or failure with [IllegalArgumentException]
         */
        fun of(value: String): Result<Hostname> =
            runCatching {
                val trimmed = value.trim().lowercase()
                require(trimmed.isNotBlank()) { "Hostname cannot be blank" }
                require(HOSTNAME_PATTERN.matches(trimmed)) {
                    "Invalid hostname format: $value"
                }
                Hostname(trimmed)
            }
    }
}
