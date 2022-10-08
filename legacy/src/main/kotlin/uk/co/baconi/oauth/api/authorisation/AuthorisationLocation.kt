package uk.co.baconi.oauth.api.authorisation

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/authorise")
data class AuthorisationLocation(
    val response_type: String? = null,
    val client_id: String? = null,
    val redirect_uri: String? = null,
    val state: String? = null,
    val scope: String? = null,
    val abort: Boolean? = null
)