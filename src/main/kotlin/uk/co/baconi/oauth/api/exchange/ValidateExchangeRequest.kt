package uk.co.baconi.oauth.api.exchange

import uk.co.baconi.oauth.api.checkNotBlank
import uk.co.baconi.oauth.api.client.ClientId
import uk.co.baconi.oauth.api.client.ClientPrincipal
import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.client.PublicClient
import uk.co.baconi.oauth.api.enums.deserialise
import uk.co.baconi.oauth.api.exchange.GrantType.*
import uk.co.baconi.oauth.api.scopes.Scopes
import io.ktor.http.*

fun validateExchangeRequest(
    principal: ConfidentialClient,
    parameters: Parameters
): ConfidentialExchangeRequest = returnOnException(InvalidConfidentialExchangeRequest) {

    // Receive the posted form, unless we implement ContentNegotiation that supports URL encoded forms.
    val raw = parameters.toRawExchangeRequest()

    when (raw.grantType) {
        AuthorisationCode -> {
            val code = checkNotBlank(raw.code) { "code" }
            val redirectUri = raw.validateRedirectUri(principal)

            // TODO - Validate the [code] is a valid code via a repository
            // TODO - Validate the [redirect_uri] is the same as what was used to generate the [code]

            AuthorisationCodeRequest(principal, code, redirectUri)
        }
        Password -> {
            val scopes = raw.validateScopes(principal)
            val username = checkNotBlank(raw.username) { "username" }
            val password = checkNotBlank(raw.password) { "password" }

            PasswordRequest(principal, scopes, username, password)
        }
        RefreshToken -> {
            val scopes = raw.validateScopes(principal)
            val refreshToken = checkNotBlank(raw.refreshToken) { "refreshToken" }

            RefreshTokenRequest(principal, scopes, refreshToken)
        }
        Assertion -> {
            val assertion = checkNotBlank(raw.assertion) { "assertion" }

            AssertionRequest(principal, assertion)
        }
        null -> InvalidConfidentialExchangeRequest // TODO - Extend to report 'unsupported_grant_type'
    }
}

fun validatePkceExchangeRequest(
    principal: PublicClient,
    parameters: Parameters
): PublicExchangeRequest = returnOnException(InvalidPublicExchangeRequest) {

    // Receive the posted form, unless we implement ContentNegotiation that supports URL encoded forms.
    val raw = parameters.toRawExchangeRequest()

    if (raw.grantType == AuthorisationCode) {

        val code = checkNotBlank(raw.code) { "code" }
        val redirectUri = raw.validateRedirectUri(principal)
        val codeVerifier = checkNotBlank(raw.codeVerifier) { "codeVerifier" }

        PkceAuthorisationCodeRequest(principal, code, redirectUri, codeVerifier)
    } else {
        InvalidPublicExchangeRequest // TODO - Invalid Grant or Request?
    }
}

// TODO - Extend to support validation failure reasons?
private fun <A : C, B : C, C> returnOnException(onException: B, block: () -> A): C = try {
    block()
} catch (exception: Exception) {
    onException
}

// TODO - See if we can extend Kotlinx Serialisation to support this instead
private fun Parameters.toRawExchangeRequest(): RawExchangeRequest {

    return RawExchangeRequest(
        // All
        grantType = get("grant_type")?.let { s -> deserialise<GrantType>(s) },

        // AuthorisationCodeRequest && PkceAuthorisationCodeRequest
        code = get("code"),
        redirectUri = get("redirect_uri"),

        // PkceAuthorisationCodeRequest
        codeVerifier = get("code_verifier"),
        clientId = get("client_id")?.let { s -> deserialise<ClientId>(s) },

        // PasswordRequest && RefreshTokenRequest
        scope = get("scope")?.split(" ")?.mapNotNull { s -> deserialise<Scopes>(s) }?.toSet(),

        // PasswordRequest
        username = get("username"),
        password = get("password"),

        // RefreshTokenRequest
        refreshToken = get("refresh_token"),

        // AssertionRequest
        assertion = get("assertion"),
    )
}

private fun RawExchangeRequest.validateScopes(principal: ConfidentialClient): Set<Scopes> {
    return scope?.filter { scope -> scope.canBeIssuedTo(principal) }?.toSet() ?: emptySet()
}

private fun Scopes.canBeIssuedTo(principal: ConfidentialClient): Boolean {
    return principal.configuration.allowedScopes.contains(this)
}

private fun RawExchangeRequest.validateRedirectUri(principal: ClientPrincipal): Url {

    val rawRedirectUri = checkNotBlank(redirectUri) { "redirectUri" }
    val redirectUrl = URLBuilder(rawRedirectUri).build()

    // Design of this system means we expect exact matches for callbacks.
    return if (principal.configuration.redirectUrls.contains(redirectUrl)) {
        redirectUrl
    } else {
        throw Exception("Invalid redirect uri: $redirectUrl")
    }
}