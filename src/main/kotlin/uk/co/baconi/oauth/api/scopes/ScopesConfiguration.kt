package uk.co.baconi.oauth.api.scopes

import org.dizitart.no2.objects.Id
import uk.co.baconi.oauth.api.userinfo.Claims

data class ScopesConfiguration(@Id val id: Scopes, val claims: Set<Claims>) {

    /**
     * Generated based on its database ID field [id].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScopesConfiguration

        if (id != other.id) return false

        return true
    }

    /**
     * Generated based on its database ID field [id].
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }
}