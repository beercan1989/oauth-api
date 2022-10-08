package uk.co.baconi.oauth.api.common.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ClientType(val value: String) {
    @SerialName("confidential") Confidential("confidential"),
    @SerialName("public") Public("public");
    companion object {
        fun fromValue(value: String): ClientType = ClientType.values().single { scope -> scope.value == value }
    }
}