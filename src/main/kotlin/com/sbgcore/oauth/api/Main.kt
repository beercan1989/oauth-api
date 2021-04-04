package com.sbgcore.oauth.api

import io.ktor.server.netty.*

/**
 * Entry point of the application: main method that starts an embedded server using Netty,
 * processes the application.conf file, interprets the command line args if available
 * and loads the application modules.
 */
fun main(args: Array<String>) {
    EngineMain.main(args)
}
