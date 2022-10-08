import kotlinx.browser.document
import react.create
import react.dom.client.createRoot
import uk.co.baconi.oauth.common.authentication.LoginForm

fun main() {

    val root = createRoot(document.getElementById("root")!!)

    root.render(LoginForm.create {
        authenticationEndpoint = "http://localhost:8083/authentication"
        csrfToken = "random-value-from-server"
    })
}