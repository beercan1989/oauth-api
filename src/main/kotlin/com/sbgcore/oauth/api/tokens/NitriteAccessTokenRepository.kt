package com.sbgcore.oauth.api.tokens

import com.sbgcore.oauth.api.client.ClientId
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.no2.IndexOptions.indexOptions
import org.dizitart.no2.IndexType.NonUnique
import org.dizitart.no2.IndexType.Unique
import org.dizitart.no2.Nitrite
import java.util.*

/**
 * A Nitrite implementation of the [AccessTokenRepository].
 *
 * - https://www.dizitart.org/nitrite-database/
 * - https://www.dizitart.org/nitrite-database/#potassium-nitrite
 */
class NitriteAccessTokenRepository(database: Nitrite) : AccessTokenRepository {

    /**
     * Create a new instance of [NitriteAccessTokenRepository] with an in-memory instance of [Nitrite]
     */
    constructor() : this(
        nitrite(userId = "access-token", password = "XqUX^CUMH90DmK3YdMpNLU#NFE1ioof7")
    )

    private val repository = database.getRepository<AccessToken> {

        // To support the day to day look up of an access token
        createIndex(AccessToken::value.name, indexOptions(Unique))

        // To support finding all access tokens for a specific customer id
        createIndex(AccessToken::customerId.name, indexOptions(NonUnique))

        // To support finding all access tokens for a specific username
        createIndex(AccessToken::username.name, indexOptions(NonUnique))

        // To support purging access tokens by client [decommissioned client || compromised client]
        createIndex(AccessToken::clientId.name, indexOptions(NonUnique))
    }

    override fun insert(new: AccessToken) {
        // TODO - Add async logic to implement automatic deletes, given this is a basic in memory nosql implementation.
        repository.insert(new) // TODO - Verify if we need to assert if token was actually inserted.
    }

    override fun delete(id: UUID) {
        repository.remove(AccessToken::id eq id)
    }

    override fun findById(id: UUID): AccessToken? {
        return repository.find(AccessToken::id eq id).firstOrDefault()
    }

    override fun findByValue(value: UUID): AccessToken? {
        return repository.find(AccessToken::value eq value).firstOrDefault()
    }

    override fun findAllByCustomerId(customerId: Long): Set<AccessToken> {
        return repository.find(AccessToken::customerId eq customerId).toSet()
    }

    override fun findAllByUsername(username: String): Set<AccessToken> {
        return repository.find(AccessToken::username eq username).toSet()
    }

    override fun findAllByClientId(clientId: ClientId): Set<AccessToken> {
        return repository.find(AccessToken::clientId eq clientId).toSet()
    }
}