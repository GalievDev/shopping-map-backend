package dev.ise.routing

import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Route.indexRoute() {
    staticResources("/", "")
}