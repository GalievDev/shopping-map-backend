package dev.ise.shoppingmap.setup.util

import dev.ise.shoppingmap.plugins.json
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*

fun ApplicationTestBuilder.jsonClient() = createClient {
    defaultRequest {
        contentType(ContentType.Application.Json)
    }
    install(ContentNegotiation) {
        json(json)
    }
}