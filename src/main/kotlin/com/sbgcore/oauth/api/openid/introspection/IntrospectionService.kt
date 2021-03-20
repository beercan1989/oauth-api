package com.sbgcore.oauth.api.openid.introspection

import com.sbgcore.oauth.api.openid.exchange.tokens.AccessToken
import com.sbgcore.oauth.api.storage.AccessTokenRepository
import java.time.OffsetDateTime.now
import java.util.*

class IntrospectionService(private val accessTokenRepository: AccessTokenRepository) {

    fun introspect(request: IntrospectionRequest) = lookup(request.token).toIntrospectionResponse()

    fun introspect(request: IntrospectionRequestWithHint) = lookup(request.token).toIntrospectionResponse()

    private fun lookup(token: String): AccessToken? = try {
        accessTokenRepository.findByValue(UUID.fromString(token))
    } catch (exception: Exception) {
        null
    }

    private fun AccessToken?.toIntrospectionResponse(): IntrospectionResponse {
        val now by lazy { now() }
        return when {
            this == null -> InactiveIntrospectionResponse()
            now.isAfter(expiresAt) -> InactiveIntrospectionResponse() // TODO - Remove from repository?
            else -> ActiveIntrospectionResponse(
                scope = scopes,
                clientId = clientId,
                username = username,
                subject = customerId.toString(),
                expirationTime = expiresAt.toEpochSecond(),
                issuedAt = issuedAt.toEpochSecond(),
                notBefore = notBefore.toEpochSecond()
            )
        }
    }
}