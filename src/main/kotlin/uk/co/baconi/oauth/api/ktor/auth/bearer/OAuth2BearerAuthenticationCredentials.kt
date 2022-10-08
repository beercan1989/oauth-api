package uk.co.baconi.oauth.api.ktor.auth.bearer

import io.ktor.auth.*
import io.ktor.http.auth.*
import io.ktor.request.*

/**
 * Retrieves Basic authentication credentials for this [ApplicationRequest]
 */
fun ApplicationRequest.oAuth2BearerAuthenticationCredentials(): OAuth2BearerCredential? {
    when (val authHeader = parseAuthorizationHeader()) {
        is HttpAuthHeader.Single -> {
            // Verify the auth scheme is HTTP Bearer. According to RFC 2617, the authorization scheme should not be case
            // sensitive; thus BEARER, or Bearer, or bearer are all valid.
            if (!authHeader.authScheme.equals("Bearer", ignoreCase = true)) {
                return null
            }

            return OAuth2BearerCredential(authHeader.blob)
        }
        else -> return null
    }
}
