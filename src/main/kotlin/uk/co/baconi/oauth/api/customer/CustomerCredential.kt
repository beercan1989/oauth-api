package uk.co.baconi.oauth.api.customer

import org.dizitart.no2.objects.Id

data class CustomerCredential(
    @Id val username: String,
    val secret: String,
    val temporary: Boolean = false,
    val locked: Boolean = false
) {
    override fun toString(): String {
        return "CustomerCredential(username='$username', secret='REDACTED', temporary=$temporary, locked=$locked)"
    }
}
