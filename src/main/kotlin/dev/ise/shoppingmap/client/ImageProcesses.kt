package dev.ise.shoppingmap.client

import dev.ise.shoppingmap.dto.Image
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ImageProcesses {
    private const val url: String = "http://10.90.136.54:5050"
    private val client: HttpClient = HttpClient(CIO).config {
        install(ContentNegotiation) {
            json(
                contentType = ContentType.Application.Json,
                json = Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    suspend fun remImgBg(image: Image): String {
        val response: HttpResponse = client.post("$url/rmbg") {
            contentType(ContentType.Application.Json)
            setBody(image)
        }

        val imageData: Image = Json.decodeFromString(response.bodyAsText())

        return imageData.bytes
    }
}