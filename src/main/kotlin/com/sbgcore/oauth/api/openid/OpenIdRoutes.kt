package com.sbgcore.oauth.api.openid

import com.sbgcore.oauth.api.openid.exchange.*
import com.sbgcore.oauth.api.openid.flows.assertion.AssertionRedemptionFlow
import com.sbgcore.oauth.api.openid.flows.authorization.AuthorizationCodeFlow
import com.sbgcore.oauth.api.openid.flows.password.PasswordFlow
import com.sbgcore.oauth.api.openid.flows.refresh.RefreshFlow
import com.sbgcore.oauth.api.openid.introspection.*
import io.ktor.application.*
import io.ktor.routing.route
import io.ktor.routing.routing

// TODO - Consider changing Application from the base library to a custom class so we don't need to include dependencies in method signature
fun Application.openIdRoutes(
    passwordFlow: PasswordFlow = PasswordFlow(),
    refreshFlow: RefreshFlow = RefreshFlow(),
    authorizationCodeFlow: AuthorizationCodeFlow = AuthorizationCodeFlow(),
    assertionRedemptionFlow: AssertionRedemptionFlow = AssertionRedemptionFlow(),
    introspectionService: IntrospectionService = IntrospectionService()
) {
    routing {
        route("/openid/v1") {
            route("/authorize") {
                // TODO - Implement
            }
            route("/token") {
                tokenExchangeRoute(passwordFlow, refreshFlow, authorizationCodeFlow, assertionRedemptionFlow)
            }
            route("/introspect") {
                // TODO - Implement
            }
            route("/revoke") {
                // TODO - Implement
            }
        }
    }
}
