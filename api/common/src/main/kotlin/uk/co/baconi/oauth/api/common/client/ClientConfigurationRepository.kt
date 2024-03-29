package uk.co.baconi.oauth.api.common.client

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import uk.co.baconi.oauth.api.common.grant.GrantType
import uk.co.baconi.oauth.api.common.scope.ScopeRepository

class ClientConfigurationRepository(scopeRepository: ScopeRepository) {

    private val clientConfiguration: Map<ClientId, ClientConfiguration> = loadClientConfiguration(scopeRepository)

    fun findById(id: ClientId): ClientConfiguration? {
        return clientConfiguration[id]
    }

    fun findByClientId(clientId: String): ClientConfiguration? {
        return clientConfiguration[ClientId(clientId)]
    }

    fun findAllClientIds(): Sequence<ClientId> {
        return clientConfiguration.keys.asSequence()
    }

    companion object {

        private val baseConfig = ConfigFactory.load()
        private val clientIds = baseConfig.getObject("uk.co.baconi.oauth.api.client.configuration").keys.map(::ClientId)
        private val clientConfiguration = baseConfig.getConfig("uk.co.baconi.oauth.api.client.configuration")

        private fun loadClientConfiguration(scopeRepository: ScopeRepository): Map<ClientId, ClientConfiguration> {
            return clientIds.associateWith { clientId ->
                val config = clientConfiguration.getConfig(clientId.value)
                ClientConfiguration(
                    id = clientId,
                    type = config
                        .getString("type")
                        .let(ClientType::fromValue),
                    redirectUris = config
                        .tryGetStringList("redirectUris")
                        ?.filter(String::isNotBlank)
                        ?.map(String::trim)
                        ?.toSet()
                        ?: emptySet(),
                    allowedScopes = config
                        .tryGetStringList("allowedScopes")
                        ?.mapNotNull(scopeRepository::findById)
                        ?.toSet()
                        ?: emptySet(),
                    allowedActions = config
                        .tryGetStringList("allowedActions")
                        ?.map(ClientAction::fromValue)
                        ?.toSet()
                        ?: emptySet(),
                    allowedGrantTypes = config
                        .tryGetStringList("allowedGrantTypes")
                        ?.map(GrantType::fromValue)
                        ?.toSet()
                        ?: emptySet(),
                )
            }
        }
    }
}