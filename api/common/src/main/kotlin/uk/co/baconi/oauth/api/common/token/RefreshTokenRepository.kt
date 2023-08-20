package uk.co.baconi.oauth.api.common.token

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.transactions.transaction
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope
import uk.co.baconi.oauth.api.common.scope.ScopesDeserializer
import uk.co.baconi.oauth.api.common.scope.ScopesSerializer
import java.time.Instant
import java.util.*

class RefreshTokenRepository(private val database: Database) {

    fun insert(new: RefreshToken) {
        transaction(database) {
            RefreshTokenTable.insertAndGetId {
                it[id] = new.value
                it[username] = new.username
                it[clientId] = new.clientId
                it[scopes] = new.scopes.let(ScopesSerializer::serialize)
                it[issuedAt] = new.issuedAt
                it[expiresAt] = new.expiresAt
                it[notBefore] = new.notBefore
            }
        }
    }

    fun findById(id: UUID): RefreshToken? {
        return transaction(database) {
            RefreshTokenTable
                .select { RefreshTokenTable.id eq id }
                .firstOrNull()
                ?.let(::toRefreshToken)
        }
    }

    fun findAllByUsername(username: AuthenticatedUsername): List<RefreshToken> {
        return transaction(database) {
            RefreshTokenTable
                .select { RefreshTokenTable.username eq username }
                .map(::toRefreshToken)
        }
    }

    fun findAllByClientId(clientId: ClientId): List<RefreshToken> {
        return transaction(database) {
            RefreshTokenTable
                .select { RefreshTokenTable.clientId eq clientId }
                .map(::toRefreshToken)
        }
    }

    fun deleteById(id: UUID) {
        transaction(database) {
            RefreshTokenTable.deleteWhere { this.id eq id }
        }
    }

    fun deleteByRecord(record: RefreshToken) {
        transaction(database) {
            RefreshTokenTable.deleteWhere { this.id eq record.value }
        }
    }

    fun deleteExpired() {
        transaction(database) {
            RefreshTokenTable.deleteWhere { this.expiresAt lessEq Instant.now() }
        }
    }

    private fun toRefreshToken(it: ResultRow): RefreshToken {
        return RefreshToken(
            value = it[RefreshTokenTable.id].value,
            username = it[RefreshTokenTable.username],
            clientId = it[RefreshTokenTable.clientId],
            scopes = it[RefreshTokenTable.scopes].let(ScopesDeserializer::deserialize).map(::Scope).toSet(),
            issuedAt = it[RefreshTokenTable.issuedAt],
            expiresAt = it[RefreshTokenTable.expiresAt],
            notBefore = it[RefreshTokenTable.notBefore],
        )
    }
}