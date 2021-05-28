package uk.co.baconi.oauth.api.authorisation

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import uk.co.baconi.oauth.api.authentication.AuthenticatedSession
import uk.co.baconi.oauth.api.authentication.AuthenticationLocation
import uk.co.baconi.oauth.api.authorisation.AuthorisationResponseType.Code
import uk.co.baconi.oauth.api.client.ClientConfigurationRepository

interface AuthorisationRoute {

    val authorisationCodeService: AuthorisationCodeService
    val clientConfigurationRepository: ClientConfigurationRepository

    fun Route.authorisation() {

        // TODO - Verify assumptions, not sure this has been done correctly
        // TODO - What about those who navigate back?
        // TODO - What about those who wish to cancel?
        // TODO - What about those logging into a new account when one already was?

        // TODO - Should we just render the AuthenticationPage on the authorisation endpoint but have an isolated endpoint?

        get<AuthorisationLocation> { location ->

            // TODO - How to detect user click back in the browser from the authentication screen
            return@get when (val request = validateAuthorisationRequest(location, clientConfigurationRepository)) {

                // Handle authorisation request [invalid] / decision [failure]
                is AuthorisationRequest.Invalid -> {

                    // TODO https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1

                    /*
                     * Error: {
                     *   error: ABC,
                     *   error_description: ABC
                     * }
                     */

                    TODO("Invalid $request")
                }

                // Handle user aborted
                is AuthorisationRequest.Aborted -> {

                    // Return with error response
                    call.respondRedirect(
                        URLBuilder(request.redirectUri).apply {
                            parameters.append("error", "access_denied")
                            parameters.append("error_description", "user aborted")
                        }.buildString()
                    )
                }

                // Handle authorisation request [valid]
                is AuthorisationRequest.Valid -> when (request.responseType) {
                    Code -> when (val authenticated = call.sessions.get<AuthenticatedSession>()) {

                        // Seek authorisation decision
                        null -> {

                            // Redirect to our "login" page with a redirect back to here.
                            call.respondRedirect(href(AuthenticationLocation(href(location))))
                        }

                        // Handle authorisation decision [success]
                        else -> {

                            val authorisationCode = authorisationCodeService.issue(request, authenticated)

                            call.respondRedirect(
                                URLBuilder().apply {
                                    takeFrom(request.redirectUri)
                                    parameters["code"] = authorisationCode.value
                                    parameters["state"] = request.state
                                }.buildString()
                            )
                        }
                    }
                }
            }
        }
    }
}