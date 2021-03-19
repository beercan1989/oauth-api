package com.sbgcore.oauth.api.openid

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ClientId : SerializableEnum {

    @SerialName("consumer-x") ConsumerX,
    @SerialName("consumer-z") ConsumerZ,

    ;

    override val value: String by lazy {
        getSerialName(serializer())
    }
}