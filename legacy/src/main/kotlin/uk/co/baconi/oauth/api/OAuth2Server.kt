package uk.co.baconi.oauth.api

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.http.*
import io.ktor.http.auth.HttpAuthHeader.*
import io.ktor.http.auth.HttpAuthHeader.Parameters
import io.ktor.server.routing.*
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.cachingheaders.CachingHeaders
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.dataconversion.DataConversion
import io.ktor.server.plugins.doublereceive.DoubleReceive
import io.ktor.server.plugins.hsts.HSTS
import io.ktor.server.resources.*
import io.ktor.server.sessions.*
import io.ktor.http.content.CachingOptions
import io.ktor.server.plugins.compression.*
import io.ktor.serialization.kotlinx.json.*
import uk.co.baconi.oauth.api.assets.StaticAssetsRoute
import uk.co.baconi.oauth.api.authentication.AuthenticatedSession
import uk.co.baconi.oauth.api.authentication.AuthenticationRoute
import uk.co.baconi.oauth.api.authentication.AuthenticationService
import uk.co.baconi.oauth.api.authentication.AuthenticationSession
import uk.co.baconi.oauth.api.authorisation.*
import uk.co.baconi.oauth.api.client.*
import uk.co.baconi.oauth.api.customer.CustomerMatchService
import uk.co.baconi.oauth.api.customer.NitriteCustomerCredentialRepository
import uk.co.baconi.oauth.api.customer.NitriteCustomerStatusRepository
import uk.co.baconi.oauth.api.exchange.ExchangeRoute
import uk.co.baconi.oauth.api.exchange.grants.assertion.AssertionRedemptionGrant
import uk.co.baconi.oauth.api.exchange.grants.authorisation.AuthorisationCodeGrant
import uk.co.baconi.oauth.api.exchange.grants.password.PasswordCredentialsGrant
import uk.co.baconi.oauth.api.exchange.grants.refresh.RefreshGrant
import uk.co.baconi.oauth.api.introspection.IntrospectionRoute
import uk.co.baconi.oauth.api.introspection.IntrospectionService
import uk.co.baconi.oauth.api.ktor.auth.basic
import uk.co.baconi.oauth.api.ktor.auth.bearer
import uk.co.baconi.oauth.api.ktor.auth.form
import uk.co.baconi.oauth.api.revocation.RevocationRoute
import uk.co.baconi.oauth.api.scopes.TypesafeScopesConfigurationRepository
import uk.co.baconi.oauth.api.swagger.SwaggerRoute
import uk.co.baconi.oauth.api.tokens.AccessToken
import uk.co.baconi.oauth.api.tokens.AccessTokenService
import uk.co.baconi.oauth.api.tokens.NitriteAccessTokenRepository
import uk.co.baconi.oauth.api.tokens.TokenAuthenticationService
import uk.co.baconi.oauth.api.userinfo.UserInfoRoute
import uk.co.baconi.oauth.api.userinfo.UserInfoService
import uk.co.baconi.oauth.api.wellknown.WellKnownRoute

object OAuth2Server : AuthenticationRoute,
    AuthorisationRoute,
    ExchangeRoute,
    IntrospectionRoute,
    RevocationRoute,
    StaticAssetsRoute,
    SwaggerRoute,
    UserInfoRoute,
    WellKnownRoute {

    const val REALM = "oauth-api"

    // Clients
    private val clientSecretRepository = NitriteClientSecretRepository()
    override val clientConfigurationRepository = TypesafeClientConfigurationRepository()
    override val clientAuthService = ClientAuthenticationService(clientSecretRepository, clientConfigurationRepository)

    // Tokens
    private val accessTokenRepository = NitriteAccessTokenRepository()
    private val accessTokenService = AccessTokenService(accessTokenRepository)
    override val introspectionService = IntrospectionService(accessTokenRepository)
    private val tokenAuthenticationService = TokenAuthenticationService(accessTokenRepository)
    private val scopesConfigurationRepository = TypesafeScopesConfigurationRepository()

    // Customer
    private val customerCredentialRepository = NitriteCustomerCredentialRepository()
    private val customerStatusRepository = NitriteCustomerStatusRepository()
    private val customerMatchService = CustomerMatchService(customerCredentialRepository)
    override val userInfoService = UserInfoService(scopesConfigurationRepository)

    // Authentication
    override val authenticationService = AuthenticationService(customerMatchService, customerStatusRepository)

    // Authorisation
    private val authorisationCodeRepository = NitriteAuthorisationCodeRepository()
    override val authorisationCodeService = AuthorisationCodeService(authorisationCodeRepository)
    private val authorisationService = AuthorisationService(authorisationCodeRepository, accessTokenService)

    // OAuth Grants
    override val passwordCredentialsGrant = PasswordCredentialsGrant(authenticationService, authorisationService)
    override val refreshGrant = RefreshGrant()
    override val authorisationCodeGrant = AuthorisationCodeGrant(authorisationCodeRepository, authorisationService)
    override val assertionRedemptionGrant = AssertionRedemptionGrant()

    fun Application.module() {

        install(Resources)
        install(AutoHeadResponse)
        install(DataConversion)

        install(HSTS) {
            includeSubDomains = true
        }

        install(ContentNegotiation) {
            json()
        }

        install(Compression) {
            gzip {
                priority = 1.0
            }
            deflate {
                priority = 10.0
                minimumSize(1024) // condition
            }
        }

        // Disable caching via headers on all requests
        install(CachingHeaders) {
            // no-store
            options { _, _ -> CachingOptions(CacheControl.NoStore(null)) }
            // no-cache
            options { _, _ -> CachingOptions(CacheControl.NoCache(null)) }
            // must-revalidate, proxy-revalidate, max-age=0
            options { _, _ -> CachingOptions(
                    CacheControl.MaxAge(
                        maxAgeSeconds = 0,
                        mustRevalidate = true,
                        proxyRevalidate = true
                    )
                )
            }
        }

        // Create some session types
        install(Sessions) {
            cookie<AuthenticationSession>("AuthenticationSession", storage = SessionStorageMemory())
            cookie<AuthenticatedSession>("AuthenticatedSession", storage = SessionStorageMemory())
        }

        // Enable `call.receive` to work twice without getting an exception
        install(DoubleReceive)

        // Graceful Shutdown
        environment.monitor.subscribe(ApplicationStopped) {
            closeAndLog(clientSecretRepository)
            closeAndLog(accessTokenRepository)
            closeAndLog(customerCredentialRepository)
            closeAndLog(customerStatusRepository)
        }

        install(Authentication) {
            basic<ConfidentialClient> {
                realm = REALM
                validate { (clientId, clientSecret) ->
                    clientAuthService.confidentialClient(clientId, clientSecret)
                }
            }
            form<PublicClient> {
                userParamName = "client_id"
                passwordParamName = "client_id"
                challenge {
                    UnauthorizedResponse(Parameterized("Body", mapOf(Parameters.Realm to REALM)))
                }
                validate { (clientId) ->
                    clientAuthService.publicClient(clientId)
                }
            }
            bearer<AccessToken> {
                realm = REALM
                validate { (token) ->
                    tokenAuthenticationService.accessToken(token)
                }
            }
        }

        routing {
            //
            // Setup UI
            //
            authentication()

            //
            // Setup the OAuth / OIDC routes
            //
            authorisation()
            exchange()
            introspection()
            revocation()
            userInfo()
            wellKnown()

            //
            // Swagger UI and spec
            //
            swagger()

            //
            // Setup some generic static assets
            //
            staticAssets()
        }
    }
}