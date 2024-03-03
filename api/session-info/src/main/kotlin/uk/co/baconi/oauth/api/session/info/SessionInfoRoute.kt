package uk.co.baconi.oauth.api.session.info

import io.ktor.http.*
import io.ktor.http.ContentType.*
import io.ktor.http.ContentType.Application
import io.ktor.http.HttpStatusCode.Companion.UnsupportedMediaType
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.util.*
import kotlinx.html.*
import uk.co.baconi.oauth.api.common.authentication.AuthenticateSession
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedSession
import uk.co.baconi.oauth.api.session.info.SessionInfoResponse.Token
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.SECONDS

interface SessionInfoRoute {

    val sessionInfoService: SessionInfoService

    fun Route.sessionInfo() {

        application.log.info("Registering the SessionInfoRoute.sessionInfo() routes")

        route("/session/info") {
            accept(Application.Json) {
                get {
                    call.respond(sessionInfoService.getSessionInfo(call.sessions.get<AuthenticatedSession>()))
                }
            }
            get {
                call.response.status(UnsupportedMediaType)
            }
        }

        route("/session") {
            accept(Text.Html) {
                get {
                    val (session, tokens) = sessionInfoService.getSessionInfo(call.sessions.get<AuthenticatedSession>())
                    call.respondHtml {
                        body {
                            h1 { +"Session Info" }
                            if (session != null) {
                                p { +"${session.username}" }
                                hr()
                            }
                            if (tokens != null) {
                                tokenTable("Access", tokens.accessTokens)
                                hr()
                                tokenTable("Refresh", tokens.refreshTokens)
                                hr()
                            }
                        }
                    }
                }
            }
            get {
                call.response.status(UnsupportedMediaType)
            }
        }
    }

    private fun FlowContent.tokenTable(type: String, tokens: List<Token>) {
        table {
            caption { +"$type Tokens" }
            tr {
                th { +"Client" }
                th { +"Issued At" }
                th { +"Expires At" }
            }
            tokens.forEach { token ->
                tr {
                    td { +"${token.clientId}" }
                    td { +"${token.issuedAt}" }
                    td { +"${token.expiresAt}" }
                }
            }
        }
    }
}