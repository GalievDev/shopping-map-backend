package dev.ise.shoppingmap

import dev.ise.shoppingmap.plugins.configureHTTP
import dev.ise.shoppingmap.plugins.configureMonitoring
import dev.ise.shoppingmap.plugins.configureRouting
import dev.ise.shoppingmap.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 5252, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureRouting()
}
