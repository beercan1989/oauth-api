package com.sbgcore.oauth.api.tokens

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Tokens {

    @SerialName("access_token") AccessToken,

    ;

}