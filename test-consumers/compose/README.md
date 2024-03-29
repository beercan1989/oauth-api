# Compose
This will be a compose multiplatform application that utilises a public client to get a user.  

Its a separate project from the main OAuth one so that the dependencies don't clash with the main application.  

Thinking of trying to cover:
* Android `./gradlew :android:installDebug`
* Desktop `./gradlew :desktop:run`
* Website (JS) `./gradlew :website:jsBrowserRun`

Probably not going to cover:
* iOS - due to needing specialised hardware and software to compile code.

Reference materials:
* https://www.jetbrains.com/lp/compose-multiplatform/
* https://github.com/JetBrains/compose-multiplatform-template#readme
* https://github.com/Kotlin/kotlin-wasm-examples/tree/main/compose-imageviewer#compose-multiplatform-for-web
* https://github.com/beercan1989/playground-compose-multiplatform#readme

Extra reading materials used:
* https://levelup.gitconnected.com/oauth-in-compose-for-desktop-with-auth0-9990075606a1
* https://github.com/sproctor/AuthCompose
