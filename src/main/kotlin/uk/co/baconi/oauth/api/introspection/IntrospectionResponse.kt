package uk.co.baconi.oauth.api.introspection

import uk.co.baconi.oauth.api.client.ClientId
import uk.co.baconi.oauth.api.scopes.Scopes
import uk.co.baconi.oauth.api.serializers.ScopeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed class IntrospectionResponse

@Serializable
data class ActiveIntrospectionResponse(
    val active: Boolean = true,
    @Serializable(with = ScopeSerializer::class) val scope: Set<Scopes>,
    @SerialName("client_id") val clientId: ClientId,
    val username: String,
    @SerialName("sub") val subject: String,
    @SerialName("exp") val expirationTime: Long,
    @SerialName("iat") val issuedAt: Long,
    @SerialName("nbf") val notBefore: Long
) : IntrospectionResponse()

@Serializable
data class InactiveIntrospectionResponse(val active: Boolean = false) : IntrospectionResponse()