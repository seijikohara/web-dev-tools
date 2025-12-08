package io.github.seijikohara.devtools.infrastructure.externalapi.rdap

import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapCidr
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapEntity
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapEvent
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapInformation
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapLink
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapNotice
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapPublicId
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapRemark
import io.github.seijikohara.devtools.domain.networkinfo.model.buildQueryUri
import io.github.seijikohara.devtools.domain.networkinfo.repository.RdapRepository
import io.github.seijikohara.devtools.domain.networkinfo.repository.RdapServerResolver
import io.github.seijikohara.devtools.infrastructure.externalapi.common.decodeJson
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import io.github.seijikohara.devtools.infrastructure.externalapi.rdap.RdapCidr as ApiRdapCidr
import io.github.seijikohara.devtools.infrastructure.externalapi.rdap.RdapEntity as ApiRdapEntity
import io.github.seijikohara.devtools.infrastructure.externalapi.rdap.RdapEvent as ApiRdapEvent
import io.github.seijikohara.devtools.infrastructure.externalapi.rdap.RdapLink as ApiRdapLink
import io.github.seijikohara.devtools.infrastructure.externalapi.rdap.RdapNotice as ApiRdapNotice
import io.github.seijikohara.devtools.infrastructure.externalapi.rdap.RdapPublicId as ApiRdapPublicId
import io.github.seijikohara.devtools.infrastructure.externalapi.rdap.RdapRemark as ApiRdapRemark

/**
 * Adapter implementation for RDAP repository.
 *
 * Fetches RDAP data from the appropriate RIR and converts to domain model.
 *
 * @property webClient HTTP client for external API calls
 * @property rdapServerResolver RDAP server resolver
 * @see <a href="https://datatracker.ietf.org/doc/rfc9083/">RFC 9083</a>
 */
class RdapRepositoryAdapter(
    private val webClient: WebClient,
    private val rdapServerResolver: RdapServerResolver,
) : RdapRepository {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Retrieves RDAP information for an IP address.
     *
     * @param ipAddress The IP address to query
     * @return [Result] containing [RdapInformation] on success, or failure with exception
     */
    override suspend fun invoke(ipAddress: IpAddress): Result<RdapInformation> =
        rdapServerResolver(ipAddress)
            .mapCatching { server -> server.buildQueryUri(ipAddress) }
            .onSuccess { uri -> logger.info("[RDAP] Fetching: $uri") }
            .mapCatching { uri ->
                webClient
                    .get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .awaitBody<String>()
            }.mapCatching { json ->
                decodeJson<RdapIpNetworkResponse>(json)
                    ?: throw IllegalStateException("Failed to decode RDAP response: received empty or null response")
            }.onFailure { e ->
                logger.error("[RDAP] Failed to parse response: ${e.message}", e)
            }.map { it.toDomain() }
}

/**
 * Converts RFC 9083 RDAP API response to domain model.
 */
private fun RdapIpNetworkResponse.toDomain(): RdapInformation =
    RdapInformation(
        objectClassName = objectClassName,
        handle = handle,
        startAddress = startAddress,
        endAddress = endAddress,
        ipVersion = ipVersion,
        name = name,
        type = type,
        country = country,
        parentHandle = parentHandle,
        status = status,
        entities = entities?.map { it.toDomain() },
        remarks = remarks?.map { it.toDomain() },
        links = links?.map { it.toDomain() },
        events = events?.map { it.toDomain() },
        port43 = port43,
        rdapConformance = rdapConformance,
        notices = notices?.map { it.toDomain() },
        lang = lang,
        cidr0Cidrs = cidr0Cidrs?.map { it.toDomain() },
        originAutnums = arinOriginas0Originautnums,
    )

private fun ApiRdapEntity.toDomain(): RdapEntity =
    RdapEntity(
        objectClassName = objectClassName,
        handle = handle,
        vcardArray = vcardArray,
        roles = roles,
        publicIds = publicIds?.map { it.toDomain() },
        entities = entities?.map { it.toDomain() },
        remarks = remarks?.map { it.toDomain() },
        links = links?.map { it.toDomain() },
        events = events?.map { it.toDomain() },
        asEventActor = asEventActor?.map { it.toDomain() },
        status = status,
        port43 = port43,
        lang = lang,
    )

private fun ApiRdapEvent.toDomain(): RdapEvent =
    RdapEvent(
        eventAction = eventAction,
        eventActor = eventActor,
        eventDate = eventDate,
        links = links?.map { it.toDomain() },
    )

private fun ApiRdapLink.toDomain(): RdapLink =
    RdapLink(
        value = value,
        rel = rel,
        href = href,
        hreflang = hreflang,
        title = title,
        media = media,
        type = type,
    )

private fun ApiRdapNotice.toDomain(): RdapNotice =
    RdapNotice(
        title = title,
        type = type,
        description = description,
        links = links?.map { it.toDomain() },
    )

private fun ApiRdapRemark.toDomain(): RdapRemark =
    RdapRemark(
        title = title,
        type = type,
        description = description,
        links = links?.map { it.toDomain() },
    )

private fun ApiRdapPublicId.toDomain(): RdapPublicId =
    RdapPublicId(
        type = type,
        identifier = identifier,
    )

private fun ApiRdapCidr.toDomain(): RdapCidr =
    RdapCidr(
        v4prefix = v4prefix,
        v6prefix = v6prefix,
        length = length,
    )
