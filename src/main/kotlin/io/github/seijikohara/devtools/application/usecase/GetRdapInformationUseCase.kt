package io.github.seijikohara.devtools.application.usecase

import io.github.seijikohara.devtools.domain.networkinfo.model.IpAddress
import io.github.seijikohara.devtools.domain.networkinfo.model.RdapInformation
import io.github.seijikohara.devtools.domain.networkinfo.repository.RdapRepository

/**
 * Use case for retrieving RDAP information for an IP address.
 *
 * This is a functional interface that encapsulates the business logic
 * for fetching RDAP data.
 */
fun interface GetRdapInformationUseCase {
    /**
     * Executes the use case.
     *
     * @param request The use case request containing the IP address string
     * @return Result containing Response if successful, or a failure with an exception
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
     * @property rdapInformation RDAP registration data for the requested IP address
     */
    data class Response(
        val rdapInformation: RdapInformation,
    )
}

/**
 * Factory function to create a GetRdapInformationUseCase instance.
 *
 * @param rdapRepository The RDAP repository implementation
 * @return A GetRdapInformationUseCase instance
 */
fun getRdapInformationUseCase(rdapRepository: RdapRepository): GetRdapInformationUseCase =
    GetRdapInformationUseCase { request ->
        IpAddress
            .of(request.ipAddressString)
            .mapCatching { ipAddress ->
                rdapRepository(ipAddress).getOrThrow()
            }.map { rdapInfo ->
                GetRdapInformationUseCase.Response(rdapInfo)
            }
    }
