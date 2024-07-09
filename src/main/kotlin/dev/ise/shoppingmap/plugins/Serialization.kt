package dev.ise.shoppingmap.plugins

import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        val converter = KotlinxSerializationConverter( Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        })
        register(ContentType.Application.Json, converter)
    }
}
