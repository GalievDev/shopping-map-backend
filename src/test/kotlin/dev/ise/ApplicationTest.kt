package dev.ise

import dev.ise.shoppingmap.plugins.configureRouting
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            install(ContentNegotiation) {
                json(
                    contentType = ContentType.Application.Json,
                    json = Json {
                        prettyPrint = true
                        ignoreUnknownKeys = true
                    }
                )
            }
            configureRouting()
        }
        client.get("/api/v1/clothes").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
/*        client.post("api/v1/clothes").apply {
            assertEquals(HttpStatusCode.Created, status)
        }*/
    }
}
