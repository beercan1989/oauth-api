package uk.co.baconi.oauth.api.common.html

import kotlinx.html.*
import uk.co.baconi.oauth.api.common.html.PageTemplate.base
import uk.co.baconi.oauth.api.common.html.PageTemplate.bootstrap
import uk.co.baconi.oauth.api.common.html.PageTemplate.csrfToken
import uk.co.baconi.oauth.api.common.html.PageTemplate.metaData
import uk.co.baconi.oauth.api.common.html.PageTemplate.redirectUri
import java.util.*

object ReactTemplate {

    fun HTML.reactPage(title: String, reactSource: String, csrfToken: UUID? = null, redirectUri: String? = null) {
        base()
        head {
            metaData()
            if (csrfToken != null) csrfToken(csrfToken)
            if (redirectUri != null) redirectUri(redirectUri)
            bootstrap()
            title { +title }
            script { defer = true; src = reactSource }
        }
        body {
            noScript { +"You need to enable JavaScript to access this website." }
            div(classes = "container") { id = "root" }
        }
    }
}