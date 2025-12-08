@file:Suppress("ktlint:standard:max-line-length")

package io.github.seijikohara.devtools.infrastructure.externalapi.rdap

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * RFC 9083 RDAP IP Network Response.
 *
 * This data class represents the complete JSON response for an IP Network object
 * as specified in RFC 9083 (JSON Responses for the Registration Data Access Protocol).
 *
 * @see <a href="https://datatracker.ietf.org/doc/rfc9083/">RFC 9083</a>
 * @see <a href="https://www.iana.org/assignments/rdap-json-values/rdap-json-values.xhtml">IANA RDAP JSON Values Registry</a>
 *
 * @property objectClassName The object class name, always "ip network" for IP network objects
 * @property handle RIR-unique identifier of the network registration (e.g., "NET-8-8-8-0-1")
 * @property startAddress Starting IP address of the network (IPv4 or IPv6 format)
 * @property endAddress Ending IP address of the network (IPv4 or IPv6 format)
 * @property ipVersion IP protocol version ("v4" for IPv4, "v6" for IPv6)
 * @property name Identifier assigned to the network registration by the registration holder
 * @property type RIR-specific classification of the network (e.g., "DIRECT ALLOCATION", "ASSIGNED")
 * @property country Two-letter ISO 3166-1 alpha-2 country code
 * @property parentHandle RIR-unique identifier of the parent network registration
 * @property status Array of status values indicating the state of the IP network (see [RdapStatus])
 * @property entities Array of entity objects related to this IP network (contacts, registrants, etc.)
 * @property remarks Array of remark objects describing the IP network
 * @property links Array of link objects related to the IP network
 * @property events Array of event objects recording the history of the IP network
 * @property port43 Hostname of the WHOIS server for this registration
 * @property rdapConformance Array of strings identifying RDAP extensions supported by the server
 * @property notices Array of notice objects at the service level (not specific to this object)
 * @property lang Language identifier for the content (BCP 47 language tag)
 * @property cidr0Cidrs Array of CIDR notation entries (RDAP extension)
 * @property arin_originas0_originautnums ARIN-specific extension for origin AS numbers
 */
@Serializable
data class RdapIpNetworkResponse(
    val objectClassName: String? = null,
    val handle: String? = null,
    val startAddress: String? = null,
    val endAddress: String? = null,
    val ipVersion: String? = null,
    val name: String? = null,
    val type: String? = null,
    val country: String? = null,
    val parentHandle: String? = null,
    val status: List<String>? = null,
    val entities: List<RdapEntity>? = null,
    val remarks: List<RdapRemark>? = null,
    val links: List<RdapLink>? = null,
    val events: List<RdapEvent>? = null,
    val port43: String? = null,
    val rdapConformance: List<String>? = null,
    val notices: List<RdapNotice>? = null,
    val lang: String? = null,
    @SerialName("cidr0_cidrs")
    val cidr0Cidrs: List<RdapCidr>? = null,
    @SerialName("arin_originas0_originautnums")
    val arinOriginas0Originautnums: List<Int>? = null,
)

/**
 * RDAP Entity object class (RFC 9083 Section 5.1).
 *
 * Represents organizations, corporations, governments, non-profits, clubs,
 * individual persons, and informal groups of people related to a registration.
 *
 * @property objectClassName The object class name, always "entity" for entity objects
 * @property handle Registry-unique identifier of the entity
 * @property vcardArray jCard array containing contact information (RFC 7095)
 * @property roles Array of role values indicating the entity's relationship to the containing object
 * @property publicIds Array of public identifier objects
 * @property entities Array of nested entity objects
 * @property remarks Array of remark objects
 * @property links Array of link objects
 * @property events Array of event objects
 * @property asEventActor Array of events where this entity is the actor
 * @property status Array of status values for this entity
 * @property port43 Hostname of the WHOIS server
 * @property lang Language identifier
 * @property networks Array of associated IP network objects (for registrars/registrants)
 * @property autnums Array of associated autonomous number objects
 */
@Serializable
data class RdapEntity(
    val objectClassName: String? = null,
    val handle: String? = null,
    val vcardArray: JsonElement? = null,
    val roles: List<String>? = null,
    val publicIds: List<RdapPublicId>? = null,
    val entities: List<RdapEntity>? = null,
    val remarks: List<RdapRemark>? = null,
    val links: List<RdapLink>? = null,
    val events: List<RdapEvent>? = null,
    val asEventActor: List<RdapEvent>? = null,
    val status: List<String>? = null,
    val port43: String? = null,
    val lang: String? = null,
    val networks: List<RdapIpNetworkResponse>? = null,
    val autnums: List<RdapAutnum>? = null,
)

/**
 * RDAP Autonomous Number object class (RFC 9083 Section 5.5).
 *
 * @property objectClassName The object class name, always "autnum"
 * @property handle Registry-unique identifier
 * @property startAutnum Starting autonomous system number in the range
 * @property endAutnum Ending autonomous system number in the range
 * @property name Identifier assigned by the registration holder
 * @property type RIR-specific classification
 * @property country Two-letter country code
 * @property status Array of status values
 * @property entities Array of related entities
 * @property remarks Array of remarks
 * @property links Array of links
 * @property events Array of events
 * @property port43 WHOIS server hostname
 */
@Serializable
data class RdapAutnum(
    val objectClassName: String? = null,
    val handle: String? = null,
    val startAutnum: Long? = null,
    val endAutnum: Long? = null,
    val name: String? = null,
    val type: String? = null,
    val country: String? = null,
    val status: List<String>? = null,
    val entities: List<RdapEntity>? = null,
    val remarks: List<RdapRemark>? = null,
    val links: List<RdapLink>? = null,
    val events: List<RdapEvent>? = null,
    val port43: String? = null,
)

/**
 * RDAP Event object (RFC 9083 Section 4.5).
 *
 * Records actions that have occurred on an object.
 *
 * Registered event action values from IANA RDAP JSON Values Registry:
 * - "registration": Initial registration
 * - "reregistration": Re-registration after removal
 * - "last changed": Last modification
 * - "expiration": When the object expires
 * - "deletion": When the object was deleted
 * - "reinstantiation": Re-registered after being removed
 * - "transfer": Transferred between registrars
 * - "locked": Object was locked
 * - "unlocked": Object was unlocked
 *
 * @property eventAction The action that occurred (see IANA registry for values)
 * @property eventActor Entity handle responsible for the action
 * @property eventDate ISO 8601 timestamp when the event occurred
 * @property links Array of related links
 */
@Serializable
data class RdapEvent(
    val eventAction: String? = null,
    val eventActor: String? = null,
    val eventDate: String? = null,
    val links: List<RdapLink>? = null,
)

/**
 * RDAP Link object (RFC 9083 Section 4.2).
 *
 * Based on RFC 8288 (Web Linking).
 *
 * @property value Context URI (the object this link is associated with)
 * @property rel Link relation type (e.g., "self", "related", "alternate")
 * @property href Target URI
 * @property hreflang Language of the target resource (BCP 47)
 * @property title Human-readable label
 * @property media Media type hint
 * @property type Expected content type of the target
 */
@Serializable
data class RdapLink(
    val value: String? = null,
    val rel: String? = null,
    val href: String? = null,
    val hreflang: List<String>? = null,
    val title: String? = null,
    val media: String? = null,
    val type: String? = null,
)

/**
 * RDAP Notice object (RFC 9083 Section 4.3).
 *
 * Service-level notices that are not specific to a particular object.
 *
 * @property title Title of the notice
 * @property type Notice type from IANA registry (e.g., "result set truncated due to authorization")
 * @property description Array of text lines forming the notice description
 * @property links Array of related links
 */
@Serializable
data class RdapNotice(
    val title: String? = null,
    val type: String? = null,
    val description: List<String>? = null,
    val links: List<RdapLink>? = null,
)

/**
 * RDAP Remark object (RFC 9083 Section 4.3).
 *
 * Object-specific remarks describing the containing object.
 * Has the same structure as [RdapNotice] but is object-specific rather than service-level.
 *
 * Registered remark/notice types from IANA RDAP JSON Values Registry:
 * - "result set truncated due to authorization"
 * - "result set truncated due to excessive load"
 * - "result set truncated due to unexplainable reasons"
 * - "object truncated due to authorization"
 * - "object truncated due to excessive load"
 * - "object truncated due to unexplainable reasons"
 *
 * @property title Title of the remark
 * @property type Remark type from IANA registry
 * @property description Array of text lines forming the remark description
 * @property links Array of related links
 */
@Serializable
data class RdapRemark(
    val title: String? = null,
    val type: String? = null,
    val description: List<String>? = null,
    val links: List<RdapLink>? = null,
)

/**
 * RDAP Public ID object (RFC 9083 Section 4.8).
 *
 * Represents a public identifier assigned to the object by a third party.
 *
 * @property type Type of public identifier (e.g., "IANA Registrar ID")
 * @property identifier The actual identifier value
 */
@Serializable
data class RdapPublicId(
    val type: String? = null,
    val identifier: String? = null,
)

/**
 * RDAP CIDR notation entry (RDAP extension).
 *
 * Represents a network in CIDR notation.
 *
 * @property v4prefix IPv4 network prefix
 * @property v6prefix IPv6 network prefix
 * @property length CIDR prefix length
 */
@Serializable
data class RdapCidr(
    val v4prefix: String? = null,
    val v6prefix: String? = null,
    val length: Int? = null,
)

/**
 * RFC 9083 RDAP Status values.
 *
 * Registered status values from IANA RDAP JSON Values Registry.
 * These values indicate the state of a registered object.
 *
 * @see <a href="https://www.iana.org/assignments/rdap-json-values/rdap-json-values.xhtml">IANA RDAP JSON Values</a>
 */
object RdapStatus {
    /** The object is in use (published in DNS for domains, allocated/assigned for networks) */
    const val ACTIVE = "active"

    /** The object is not in use */
    const val INACTIVE = "inactive"

    /** Changes to the object cannot be made */
    const val LOCKED = "locked"

    /** A create request is pending */
    const val PENDING_CREATE = "pending create"

    /** A renewal request is pending */
    const val PENDING_RENEW = "pending renew"

    /** A transfer request is pending */
    const val PENDING_TRANSFER = "pending transfer"

    /** An update request is pending */
    const val PENDING_UPDATE = "pending update"

    /** A delete request is pending */
    const val PENDING_DELETE = "pending delete"

    /** The data has been validated as accurate */
    const val VALIDATED = "validated"

    /** Renewal is forbidden */
    const val RENEW_PROHIBITED = "renew prohibited"

    /** Updates are forbidden */
    const val UPDATE_PROHIBITED = "update prohibited"

    /** Transfers are forbidden */
    const val TRANSFER_PROHIBITED = "transfer prohibited"

    /** Deletion is forbidden */
    const val DELETE_PROHIBITED = "delete prohibited"

    /** Registration performed by a third party (proxy) */
    const val PROXY = "proxy"

    /** Information is not designated for public consumption */
    const val PRIVATE = "private"

    /** Some information has been removed */
    const val REMOVED = "removed"

    /** Object contains redacted data due to lack of authorization */
    const val OBSCURED = "obscured"

    /** Object has been linked to other objects in the registry */
    const val ASSOCIATED = "associated"

    /** Allocated administratively, not for operational networks */
    const val ADMINISTRATIVE = "administrative"

    /** Allocated to an IANA special-purpose address registry */
    const val RESERVED = "reserved"

    // EPP-to-RDAP mapped status values (RFC 8056)

    /** Add grace period */
    const val ADD_PERIOD = "add period"

    /** Auto-renew grace period */
    const val AUTO_RENEW_PERIOD = "auto renew period"

    /** Client-requested delete prohibition */
    const val CLIENT_DELETE_PROHIBITED = "client delete prohibited"

    /** Client-requested hold */
    const val CLIENT_HOLD = "client hold"

    /** Client-requested renew prohibition */
    const val CLIENT_RENEW_PROHIBITED = "client renew prohibited"

    /** Client-requested transfer prohibition */
    const val CLIENT_TRANSFER_PROHIBITED = "client transfer prohibited"

    /** Client-requested update prohibition */
    const val CLIENT_UPDATE_PROHIBITED = "client update prohibited"

    /** Redemption grace period */
    const val REDEMPTION_PERIOD = "redemption period"

    /** Renew grace period */
    const val RENEW_PERIOD = "renew period"

    /** Server-side delete prohibition */
    const val SERVER_DELETE_PROHIBITED = "server delete prohibited"

    /** Server-side renew prohibition */
    const val SERVER_RENEW_PROHIBITED = "server renew prohibited"

    /** Server-side transfer prohibition */
    const val SERVER_TRANSFER_PROHIBITED = "server transfer prohibited"

    /** Server-side update prohibition */
    const val SERVER_UPDATE_PROHIBITED = "server update prohibited"

    /** Server-side hold */
    const val SERVER_HOLD = "server hold"

    /** Transfer grace period */
    const val TRANSFER_PERIOD = "transfer period"

    /** Pending restore */
    const val PENDING_RESTORE = "pending restore"
}

/**
 * RFC 9083 RDAP Event Action values.
 *
 * Registered event action values from IANA RDAP JSON Values Registry.
 *
 * @see <a href="https://www.iana.org/assignments/rdap-json-values/rdap-json-values.xhtml">IANA RDAP JSON Values</a>
 */
object RdapEventAction {
    /** The object was initially registered */
    const val REGISTRATION = "registration"

    /** The object was re-registered after being removed */
    const val REREGISTRATION = "reregistration"

    /** The object was last changed/modified */
    const val LAST_CHANGED = "last changed"

    /** The object expires from the registry */
    const val EXPIRATION = "expiration"

    /** The object was deleted */
    const val DELETION = "deletion"

    /** The object was reinstantiated after removal */
    const val REINSTANTIATION = "reinstantiation"

    /** The object was transferred between registrars */
    const val TRANSFER = "transfer"

    /** The object was locked */
    const val LOCKED = "locked"

    /** The object was unlocked */
    const val UNLOCKED = "unlocked"

    // Additional event actions from various RFCs

    /** Last update of RDAP database */
    const val LAST_UPDATE_OF_RDAP_DATABASE = "last update of RDAP database"

    /** Registrar expiration */
    const val REGISTRAR_EXPIRATION = "registrar expiration"

    /** Enum validation expiration */
    const val ENUM_VALIDATION_EXPIRATION = "enum validation expiration"
}

/**
 * RFC 9083 RDAP Role values.
 *
 * Registered role values from IANA RDAP JSON Values Registry.
 * These indicate the relationship between an entity and the object containing it.
 *
 * @see <a href="https://www.iana.org/assignments/rdap-json-values/rdap-json-values.xhtml">IANA RDAP JSON Values</a>
 */
object RdapRole {
    /** The entity is the registrant of the registration */
    const val REGISTRANT = "registrant"

    /** The entity is a technical contact */
    const val TECHNICAL = "technical"

    /** The entity is an administrative contact */
    const val ADMINISTRATIVE = "administrative"

    /** The entity handles billing for the registration */
    const val BILLING = "billing"

    /** The entity handles network abuse issues */
    const val ABUSE = "abuse"

    /** The entity is the registrar responsible for the registration */
    const val REGISTRAR = "registrar"

    /** The entity is the reseller */
    const val RESELLER = "reseller"

    /** The entity is the sponsor */
    const val SPONSOR = "sponsor"

    /** The entity is a proxy for the actual registrant */
    const val PROXY = "proxy"

    /** The entity provides notifications */
    const val NOTIFICATIONS = "notifications"

    /** No specific role */
    const val NOC = "noc"
}
