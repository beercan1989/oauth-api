package uk.co.baconi.oauth.api.authorisation

import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.DatabaseFactory
import uk.co.baconi.oauth.api.common.DatabaseFactory.authorisationCodeDatabase
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
import uk.co.baconi.oauth.api.common.client.ClientConfigurationRepository
import uk.co.baconi.oauth.api.common.embeddedCommonServer

/**
 * Start a server for just Authorisation requests
 */
internal object AuthorisationServer : AuthorisationRoute {

    private val authorisationCodeRepository = AuthorisationCodeRepository(authorisationCodeDatabase)
    override val authorisationCodeService = AuthorisationCodeService(authorisationCodeRepository)

    override val clientConfigurationRepository = ClientConfigurationRepository()

    fun start() {
        embeddedCommonServer {
            common()
            routing {
                authorisation()
            }
        }.start(true)
    }
}