package uk.co.baconi.oauth.api.client

import uk.co.baconi.oauth.api.openid.Scopes
import io.ktor.http.*

data class ClientConfiguration(
    val id: ClientId,
    val type: ClientType,
    val redirectUrls: Set<Url>,
    val allowedScopes : Set<Scopes>
) {
    val isConfidential = type == ClientType.Confidential
    val isPublic = type == ClientType.Public
}