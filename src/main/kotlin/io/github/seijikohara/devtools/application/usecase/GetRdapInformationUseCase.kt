package io.github.seijikohara.devtools.application.usecase

import io.github.seijikohara.devtools.domain.common.extensions.flatMap
import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapInformation
import io.github.seijikohara.devtools.domain.networkinfo.repository.RdapRepository

/**
 * Use case for retrieving RDAP information for an IP address.
 *
 * @see Request
 * @see Response
 * @see RdapInformation
 */
fun interface GetRdapInformationUseCase {
    /**
     * Retrieves RDAP information for an IP address.
     *
     * @param request The request containing the IP address string
     * @return [Result] containing [Response] on success, or failure with exception
     */
    suspend operator fun invoke(request: Request): Result<Response>

    /**
     * Request for retrieving RDAP information.
     *
     * @property ipAddressString IP address in string format
     */
    data class Request(
        val ipAddressString: String,
    )

    /**
     * Response containing RDAP information.
     *
     * @property rdapInformation RDAP registration data
     */
    data class Response(
        val rdapInformation: RdapInformation,
    )
}

/**
 * Creates a [GetRdapInformationUseCase] instance.
 *
 * @param rdapRepository The RDAP repository implementation
 * @return A [GetRdapInformationUseCase] instance
 */
fun getRdapInformationUseCase(rdapRepository: RdapRepository): GetRdapInformationUseCase =
    GetRdapInformationUseCase { request ->
        IpAddress
            .of(request.ipAddressString)
            .flatMap { ipAddress ->
                rdapRepository(ipAddress)
            }.map { rdapInfo ->
                GetRdapInformationUseCase.Response(rdapInfo)
            }
    }
