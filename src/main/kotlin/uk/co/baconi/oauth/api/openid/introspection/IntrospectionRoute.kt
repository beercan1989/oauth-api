package uk.co.baconi.oauth.api.openid.introspection

import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.ktor.auth.authenticate
import uk.co.baconi.oauth.api.ktor.auth.extractClient
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

interface IntrospectionRoute {

    val introspectionService: IntrospectionService

    fun Route.introspectionRoute() {
        authenticate(ConfidentialClient::class) {
            post {
                extractClient<ConfidentialClient> { principal ->

                    val response = when (val request = validateIntrospectionRequest(principal)) {
                        is IntrospectionRequest -> introspectionService.introspect(request)
                        is IntrospectionRequestWithHint -> introspectionService.introspect(request)
                    }

                    call.respond(response)
                }
            }
        }
    }
}