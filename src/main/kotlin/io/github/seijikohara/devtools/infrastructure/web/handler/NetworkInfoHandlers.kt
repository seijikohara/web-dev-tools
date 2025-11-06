package io.github.seijikohara.devtools.infrastructure.web.handler

import io.github.seijikohara.devtools.application.usecase.GetGeoLocationUseCase
import io.github.seijikohara.devtools.application.usecase.GetRdapInformationUseCase
import io.github.seijikohara.devtools.infrastructure.externalapi.rdap.RdapServerNotFoundException
import io.github.seijikohara.devtools.infrastructure.web.dto.toDto
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait

/**
 * Handler function for RDAP information endpoint.
 *
 * @param request The server request containing the IP address path variable
 * @param useCase The use case for retrieving RDAP information
 * @return ServerResponse with RDAP data or error response
 */
suspend fun handleGetRdapInformation(
    request: ServerRequest,
    useCase: GetRdapInformationUseCase,
): ServerResponse =
    useCase(GetRdapInformationUseCase.Request(request.pathVariable("ip")))
        .fold(
            onSuccess = { response ->
                ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValueAndAwait(response.toDto())
            },
            onFailure = { error ->
                handleNetworkInfoError(error)
            },
        )

/**
 * Handler function for GeoIP information endpoint.
 *
 * @param request The server request containing the IP address path variable
 * @param useCase The use case for retrieving GeoIP information
 * @return ServerResponse with GeoIP data or error response
 */
suspend fun handleGetGeoLocation(
    request: ServerRequest,
    useCase: GetGeoLocationUseCase,
): ServerResponse =
    useCase(GetGeoLocationUseCase.Request(request.pathVariable("ip")))
        .fold(
            onSuccess = { response ->
                ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValueAndAwait(response.toDto())
            },
            onFailure = { error ->
                handleNetworkInfoError(error)
            },
        )

/**
 * Common error handler for network information endpoints.
 *
 * @param error The exception to handle
 * @return ServerResponse with appropriate error status
 */
private suspend fun handleNetworkInfoError(error: Throwable): ServerResponse =
    when (error) {
        is RdapServerNotFoundException ->
            ServerResponse.notFound().buildAndAwait()

        is IllegalArgumentException ->
            ServerResponse.badRequest().buildAndAwait()

        else -> throw error
    }
