package dev.ise.shoppingmap.client

import dev.ise.shoppingmap.dto.Cloth
import dev.ise.shoppingmap.dto.Image
import dev.ise.shoppingmap.request.ClothRequest
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ImageProcesses {
    private const val url: String = "http://10.90.136.54"
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
        val response: HttpResponse = client.post("$url:5050/rmbg/") {
            contentType(ContentType.Application.Json)
            setBody(image)
        }

        val imageData: Image = Json.decodeFromString(response.bodyAsText())

        return imageData.bytes
    }

    suspend fun generateOutfitImage(clothesIds: List<Int>): String {
        val clothes = mutableListOf<Cloth>()
        val clothesRequests = mutableListOf<ClothRequest>()
        val images = mutableListOf<Image>()

        clothesIds.forEach {
            val responseClothes: HttpResponse = client.get("$url:5252/api/v1/clothes/$it") {
                contentType(ContentType.Application.Json)
            }
            val cloth: Cloth = Json.decodeFromString(responseClothes.bodyAsText())
            clothes.add(cloth)
        }


        clothes.forEach{
            val responseImages: HttpResponse = client.get("$url:5252/api/v1/images/${it.image_id}") {
                contentType(ContentType.Application.Json)
            }

            val image: Image = Json.decodeFromString(responseImages.bodyAsText())
            val clothRequest = ClothRequest(it.name, it.link, it.description, it.type, image.bytes)
            clothesRequests.add(clothRequest)
            images.add(image)
        }

        val response: HttpResponse = client.post("$url:5050/generate_outfit/") {
            contentType(ContentType.Application.Json)
            setBody(clothesRequests)
        }

        val generatedImage: Image = Json.decodeFromString(response.bodyAsText())

        return generatedImage.bytes
    }
}