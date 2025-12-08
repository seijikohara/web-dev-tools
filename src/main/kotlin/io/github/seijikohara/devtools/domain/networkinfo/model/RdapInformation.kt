@file:Suppress("ktlint:standard:max-line-length")

package io.github.seijikohara.devtools.domain.networkinfo.model

/**
 * Represents complete RDAP (Registry Data Access Protocol) information for an IP network.
 *
 * This domain model encompasses all registration data that can be obtained from RDAP queries
 * as specified in RFC 9083.
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
 * @property status Array of status values indicating the state of the IP network
 * @property entities Array of entity objects related to this IP network (contacts, registrants, etc.)
 * @property remarks Array of remark objects describing the IP network
 * @property links Array of link objects related to the IP network
 * @property events Array of event objects recording the history of the IP network
 * @property port43 Hostname of the WHOIS server for this registration
 * @property rdapConformance Array of strings identifying RDAP extensions supported by the server
 * @property notices Array of notice objects at the service level (not specific to this object)
 * @property lang Language identifier for the content (BCP 47 language tag)
 * @property cidr0Cidrs Array of CIDR notation entries (RDAP extension)
 * @property originAutnums ARIN-specific extension for origin AS numbers
 *
 * @see RdapEntity
 * @see RdapEvent
 * @see RdapLink
 * @see RdapRemark
 * @see RdapNotice
 * @see <a href="https://datatracker.ietf.org/doc/rfc9083/">RFC 9083</a>
 */
data class RdapInformation(
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
    val cidr0Cidrs: List<RdapCidr>? = null,
    val originAutnums: List<Int>? = null,
)

/**
 * RDAP Entity object class (RFC 9083 Section 5.1).
 *
 * Represents organizations, corporations, governments, non-profits, clubs,
 * individual persons, and informal groups of people related to a registration.
 *
 * @property objectClassName The object class name, always "entity" for entity objects
 * @property handle Registry-unique identifier of the entity
 * @property vcardArray jCard array containing contact information (RFC 7095) as raw data
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
 */
data class RdapEntity(
    val objectClassName: String? = null,
    val handle: String? = null,
    val vcardArray: Any? = null,
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
)

/**
 * RDAP Event object (RFC 9083 Section 4.5).
 *
 * Records actions that have occurred on an object.
 *
 * @property eventAction The action that occurred (see IANA registry for values)
 * @property eventActor Entity handle responsible for the action
 * @property eventDate ISO 8601 timestamp when the event occurred
 * @property links Array of related links
 */
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
 * @property type Notice type from IANA registry
 * @property description Array of text lines forming the notice description
 * @property links Array of related links
 */
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
 *
 * @property title Title of the remark
 * @property type Remark type from IANA registry
 * @property description Array of text lines forming the remark description
 * @property links Array of related links
 */
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
data class RdapCidr(
    val v4prefix: String? = null,
    val v6prefix: String? = null,
    val length: Int? = null,
)
