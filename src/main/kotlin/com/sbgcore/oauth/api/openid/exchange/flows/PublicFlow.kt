package com.sbgcore.oauth.api.openid.exchange.flows

import com.sbgcore.oauth.api.openid.exchange.ExchangeResponse
import com.sbgcore.oauth.api.openid.exchange.ValidatedPublicExchangeRequest

interface PublicFlow<A : ValidatedPublicExchangeRequest> {
    suspend fun exchange(request: A): ExchangeResponse
}