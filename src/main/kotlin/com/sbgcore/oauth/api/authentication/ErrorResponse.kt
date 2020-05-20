package com.sbgcore.oauth.api.authentication

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val error: String, @SerialName("error_description") val description: String = "")
