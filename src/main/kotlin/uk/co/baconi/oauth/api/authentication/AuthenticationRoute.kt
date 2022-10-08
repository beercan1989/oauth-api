package uk.co.baconi.oauth.api.authentication

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.FormUrlEncoded
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.html.*
import uk.co.baconi.oauth.api.ktor.ApplicationContext
import uk.co.baconi.oauth.api.ktor.resourcesFormat
import java.util.*

interface AuthenticationRoute {

    val authenticationService: AuthenticationService

    private fun ApplicationContext.getAuthenticationSession(): AuthenticationSession {
        return call.sessions.getOrSet { AuthenticationSession(UUID.randomUUID()) }
    }

    fun Route.authentication() {

        // TODO - Consider only support post and requiring the initial render to happen from the AuthorisationLocation
        get<AuthenticationLocation> { location ->
            // TODO - Do we invalidate the AuthenticatedSession on first render?
            // TODO - Consider adding support for just password entry if we have an AuthenticatedSession
            renderAuthenticationPage(location)
        }

        contentType(FormUrlEncoded) {
            post<AuthenticationLocation> { location ->

                return@post when (val request = validateAuthenticationRequest(location)) {

                    is AuthenticationRequest.InvalidCsrf -> {
                        renderAuthenticationPage(location, request, Forbidden) {
                            +"Please check and try again, we received an invalid CSRF token."
                        }
                    }

                    is AuthenticationRequest.InvalidFields -> {
                        renderAuthenticationPage(location, request, BadRequest) {
                            +"Please fill out and retry, we need both your username and password to log you in."
                        }
                    }

                    is AuthenticationRequest.Aborted -> {

                        // Destroy pre-authenticated session.
                        call.sessions.clear<AuthenticationSession>()

                        // Go to redirect uri
                        call.respondRedirect {
                            parameters.clear()
                            takeFrom(request.redirect)
                            parameters["abort"] = "true"
                        }
                    }

                    is AuthenticationRequest.Valid -> when (val result = authenticationService.authenticate(request)) {

                        is Authentication.Failure -> renderAuthenticationPage(location, request, Unauthorized) {
                            +"Please check and try again or if you have forgotten your details, recover them "
                            a(
                                href = "/recovery",
                                classes = "alert-link"
                            ) { +"here" } // TODO - Implement recovery mechanism?
                            +"."
                        }

                        is Authentication.Success -> {
                            // Setup the authenticated session.
                            call.sessions.set(AuthenticatedSession(result.username))

                            // Destroy pre-authenticated session.
                            call.sessions.clear<AuthenticationSession>()

                            // Go to redirect uri
                            call.respondRedirect {
                                parameters.clear()
                                takeFrom(request.redirect)
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun ApplicationContext.renderAuthenticationPage(
        location: AuthenticationLocation,
        request: AuthenticationRequest? = null,
        status: HttpStatusCode = HttpStatusCode.OK,
        failureMessage: (DIV.(Placeholder<DIV>) -> Unit)? = null
    ) {
        val session = getAuthenticationSession()

        // TODO - Move more into the template than out here.
        call.respondHtmlTemplate(AuthenticationPageTemplate(resourcesFormat, location), status) {
            csrfToken {
                value = session.csrfToken
            }
            if (request != null) {
                username {
                    fillIn(request.username)
                }
                password {
                    fillIn(request.password)
                }
            }
            if (failureMessage != null) {
                beforeInput {
                    div("alert alert-danger") {
                        role = "alert"
                        insert(Placeholder<DIV>().apply { invoke(content = failureMessage) })
                    }
                }
            }
        }
    }

    // TODO - Decide if this is good or bad pattern
    private fun INPUT.fillIn(data: String?) {
        if (data.isNullOrBlank()) {
            classes = classes + "is-invalid"
        } else {
            value = data
            classes = classes + "is-valid"
        }
    }
}