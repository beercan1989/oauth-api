package uk.co.baconi.oauth.api.token

import uk.co.baconi.oauth.api.common.client.ConfidentialClient
import uk.co.baconi.oauth.api.common.scope.Scope

class PasswordRequest(
    override val principal: ConfidentialClient,
    val scopes: Set<Scope>,
    val username: String,
    val password: CharArray
) : TokenRequest.Valid<ConfidentialClient> {
    override fun toString(): String {
        return "PasswordRequest(principal=$principal, scopes=$scopes, username='$username', password='REDACTED')"
    }
}