package uk.co.baconi.oauth.api.authorisation

import uk.co.baconi.oauth.api.client.ClientId
import uk.co.baconi.oauth.api.scopes.Scopes

sealed class AuthorisationRequest {

    object Invalid : AuthorisationRequest()

    data class Aborted(val redirectUri: String) : AuthorisationRequest()

    data class Valid(
        val responseType: ResponseType,
        val clientId: ClientId,
        val redirectUri: String,
        val state: String,
        val requestedScope: Set<Scopes>
    ) : AuthorisationRequest() {

        /**
         * Generated to exclude [state] from the toString output.
         */
        override fun toString(): String {
            return "Valid(responseType=$responseType, clientId=$clientId, redirectUri=$redirectUri, state='REDACTED', requestedScope=$requestedScope)"
        }
    }
}