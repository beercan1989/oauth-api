package uk.co.baconi.oauth.api.server

import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.common.AuthenticationModule
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.DatabaseFactory.getAccessTokenDatabase
import uk.co.baconi.oauth.api.common.DatabaseFactory.getAuthorisationCodeDatabase
import uk.co.baconi.oauth.api.common.DatabaseFactory.getCustomerCredentialDatabase
import uk.co.baconi.oauth.api.common.DatabaseFactory.getCustomerStatusDatabase
import uk.co.baconi.oauth.api.common.TestDataModule
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
import uk.co.baconi.oauth.api.common.client.ClientConfigurationRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretService
import uk.co.baconi.oauth.common.authentication.CustomerAuthenticationService
import uk.co.baconi.oauth.common.authentication.CustomerCredentialRepository
import uk.co.baconi.oauth.common.authentication.CustomerStatusRepository
import uk.co.baconi.oauth.api.common.embeddedCommonServer
import uk.co.baconi.oauth.api.common.token.AccessTokenRepository
import uk.co.baconi.oauth.api.common.token.AccessTokenService
import uk.co.baconi.oauth.api.token.AuthorisationCodeGrant
import uk.co.baconi.oauth.api.token.PasswordGrant
import uk.co.baconi.oauth.api.token.TokenRoute
import uk.co.baconi.oauth.api.token.introspection.IntrospectionRoute
import uk.co.baconi.oauth.api.token.introspection.IntrospectionService

object FullServer : AuthenticationModule, TokenRoute, IntrospectionRoute, TestDataModule {

    private val accessTokenRepository = AccessTokenRepository(getAccessTokenDatabase())
    override val accessTokenService = AccessTokenService(accessTokenRepository)

    private val clientSecretRepository = ClientSecretRepository()
    private val clientConfigurationRepository = ClientConfigurationRepository()
    override val clientSecretService = ClientSecretService(clientSecretRepository, clientConfigurationRepository)

    override val authorisationCodeGrant = AuthorisationCodeGrant(accessTokenService)
    override val authorisationCodeRepository = AuthorisationCodeRepository(getAuthorisationCodeDatabase())

    private val customerStatusRepository = CustomerStatusRepository(getCustomerStatusDatabase())
    private val customerCredentialRepository = CustomerCredentialRepository(getCustomerCredentialDatabase())
    private val customerAuthenticationService = CustomerAuthenticationService(customerCredentialRepository, customerStatusRepository)

    override val passwordGrant = PasswordGrant(accessTokenService, customerAuthenticationService)

    override val introspectionService = IntrospectionService(accessTokenRepository)

    fun start() {
        embeddedCommonServer {
            common()
            authentication()
            routing {
                token()
                introspection()
            }
            generateTestData() // TODO - Remove
        }.start(true)
    }
}