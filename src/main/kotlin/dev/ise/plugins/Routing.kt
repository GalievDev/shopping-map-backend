package dev.ise.plugins

import dev.ise.routing.indexRoute
import dev.ise.routing.v1.clothes
import dev.ise.routing.v1.images
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        indexRoute()
        route("api/v1") {
            clothes()
            images()
        }
    }
}
