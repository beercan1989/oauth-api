package uk.co.baconi.oauth.api.token

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TokenErrorType {
    /**
     * The request is missing a required parameter, includes an unsupported parameter value (other than grant type),
     * repeats a parameter, includes multiple credentials, utilizes more than one mechanism for authenticating the
     * client, or is otherwise malformed.
     */
    @SerialName("invalid_request") InvalidRequest,

    /**
     * Client authentication failed (e.g., unknown client, no client authentication included, or unsupported
     * authentication method).  The authorization server MAY return an HTTP 401 (Unauthorized) status code to indicate
     * which HTTP authentication schemes are supported.  If the client attempted to authenticate via the "Authorization"
     * request header field, the authorization server MUST respond with an HTTP 401 (Unauthorized) status code and
     * include the "WWW-Authenticate" response header field matching the authentication scheme used by the client.
     */
    @SerialName("invalid_client") InvalidClient,

    /**
     * The provided authorization grant (e.g., authorization code, resource owner credentials) or refresh token is
     * invalid, expired, revoked, does not match the redirection URI used in the authorization request, or was issued to
     * another client.
     */
    @SerialName("invalid_grant") InvalidGrant,

    /**
     * The authenticated client is not authorised to use this authorization grant type.
     */
    @SerialName("unauthorized_client") UnauthorizedClient,

    /**
     * The authorization grant type is not supported by the authorization server.
     */
    @SerialName("unsupported_grant_type") UnsupportedGrantType,

    /**
     * The requested scope is invalid, unknown, malformed, or exceeds the scope granted by the resource owner.
     */
    @SerialName("invalid_scope") InvalidScope,
}