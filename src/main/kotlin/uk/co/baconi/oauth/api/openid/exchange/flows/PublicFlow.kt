package uk.co.baconi.oauth.api.openid.exchange.flows

import uk.co.baconi.oauth.api.openid.exchange.ExchangeResponse
import uk.co.baconi.oauth.api.openid.exchange.ValidatedPublicExchangeRequest

interface PublicFlow<A : ValidatedPublicExchangeRequest> {
    suspend fun exchange(request: A): ExchangeResponse
}