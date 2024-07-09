package dev.ise.shoppingmap.client

import dev.ise.shoppingmap.dto.Cloth
import dev.ise.shoppingmap.dto.ClothType
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
        val typeOrder = listOf(ClothType.TOP, ClothType.OUTWEAR, ClothType.UNDERWEAR, ClothType.FOOTWEAR, ClothType.ACCESSORY, ClothType.NONE)
        val clothes = mutableListOf<Cloth>()
        val images = mutableListOf<Image>()

        clothesIds.forEach {
            val responseClothes: HttpResponse = client.get("$url:5252/api/v1/clothes/$it") {
                contentType(ContentType.Application.Json)
            }
            val cloth: Cloth = Json.decodeFromString(responseClothes.bodyAsText())
            clothes.add(cloth)
        }

        clothes.sortWith(compareBy { typeOrder.indexOf(it.type) })

        clothes.forEach{
            val responseImages: HttpResponse = client.get("$url:5252/api/v1/images/${it.image_id}") {
                contentType(ContentType.Application.Json)
            }

            val image: Image = Json.decodeFromString(responseImages.bodyAsText())
            images.add(image)
        }

        val response: HttpResponse = client.post("$url:5050/generate_outfit/") {
            contentType(ContentType.Application.Json)
            setBody(images)
        }

        val generatedImage: Image = Json.decodeFromString(response.bodyAsText())

        return generatedImage.bytes
    }
}