package dev.ise.shoppingmap.client

import dev.ise.shoppingmap.dto.Cloth
import dev.ise.shoppingmap.dto.Image
import dev.ise.shoppingmap.dto.Outfit
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
    private const val URL: String = "http://51.250.36.103"
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
        val response: HttpResponse = client.post("$URL:5050/rmbg/") {
            contentType(ContentType.Application.Json)
            setBody(image)
        }

        val imageData: Image = Json.decodeFromString(response.bodyAsText())

        return imageData.bytes
    }

    suspend fun generateOutfitImage(clothesIds: List<Int>): String {
        val clothes = mutableListOf<Cloth>()
        val clothesRequests = mutableListOf<ClothRequest>()

        clothesIds.forEach {
            val responseClothes: HttpResponse = client.get("$URL:5252/api/v1/clothes/$it") {
                contentType(ContentType.Application.Json)
            }
            val cloth: Cloth = Json.decodeFromString(responseClothes.bodyAsText())
            clothes.add(cloth)
        }

        clothes.forEach{
            val responseImages: HttpResponse = client.get("$URL:5252/api/v1/images/${it.imageId}") {
                contentType(ContentType.Application.Json)
            }

            val image: Image = Json.decodeFromString(responseImages.bodyAsText())
            val clothRequest = ClothRequest(it.name, it.link, it.description, it.type, image.bytes)
            clothesRequests.add(clothRequest)
        }

        val response: HttpResponse = client.post("$URL:5050/generate_outfit/") {
            contentType(ContentType.Application.Json)
            setBody(clothesRequests)
        }

        val generatedImage: Image = Json.decodeFromString(response.bodyAsText())

        return generatedImage.bytes
    }

    suspend fun generateCapsuleImage(outfitsIds: List<Int>): String {
        val outfits = mutableListOf<Outfit>()
        val clothes = mutableListOf<Cloth>()
        val clothesRequests = mutableListOf<ClothRequest>()

        outfitsIds.forEach {
            val responseOutfits: HttpResponse = client.get("$URL:5252/api/v1/outfits/$it") {
                contentType(ContentType.Application.Json)
            }

            val outfit: Outfit = Json.decodeFromString(responseOutfits.bodyAsText())
            outfits.add(outfit)
        }

        outfits.forEach {
            it.clothes.forEach { id ->
                val responseClothes: HttpResponse = client.get("$URL:5252/api/v1/clothes/${id}") {
                    contentType(ContentType.Application.Json)
                }

                val cloth: Cloth = Json.decodeFromString(responseClothes.bodyAsText())
                clothes.add(cloth)
            }
        }

        clothes.forEach{
            val responseImages: HttpResponse = client.get("$URL:5252/api/v1/images/${it.imageId}") {
                contentType(ContentType.Application.Json)
            }

            val image: Image = Json.decodeFromString(responseImages.bodyAsText())
            val clothRequest = ClothRequest(it.name, it.link, it.description, it.type, image.bytes)
            clothesRequests.add(clothRequest)
        }

        val response: HttpResponse = client.post("$URL:5050/generate_capsule/") {
            contentType(ContentType.Application.Json)
            setBody(clothesRequests)
        }

        println(response.bodyAsText())
        val generatedImage: Image = Json.decodeFromString(response.bodyAsText())
        return generatedImage.bytes
    }
}