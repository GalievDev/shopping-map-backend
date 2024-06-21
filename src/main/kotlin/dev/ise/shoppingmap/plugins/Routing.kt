package dev.ise.shoppingmap.plugins

import dev.ise.shoppingmap.routing.indexRoute
import dev.ise.shoppingmap.routing.v1.capsules
import dev.ise.shoppingmap.routing.v1.clothes
import dev.ise.shoppingmap.routing.v1.images
import dev.ise.shoppingmap.routing.v1.outfits
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        indexRoute()
        route("api/v1") {
            clothes()
            images()
            outfits()
            capsules()
        }
    }
}
