package com.sbgcore.oauth.api.openid.exchange.flows

import com.sbgcore.oauth.api.openid.exchange.ExchangeResponse
import com.sbgcore.oauth.api.openid.exchange.ValidatedConfidentialExchangeRequest

interface ConfidentialFlow<A : ValidatedConfidentialExchangeRequest> {
    suspend fun exchange(request: A): ExchangeResponse
}