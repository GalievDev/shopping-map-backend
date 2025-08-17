package dev.ise.shoppingmap.client

import dev.ise.shoppingmap.dto.Cloth
import dev.ise.shoppingmap.dto.Image
import dev.ise.shoppingmap.dto.Outfit
import dev.ise.shoppingmap.dto.request.ClothRequest
import dev.ise.shoppingmap.mics.IMAGE_MODULE_URL
import dev.ise.shoppingmap.repository.postgre.PostgresClothRepository
import dev.ise.shoppingmap.repository.postgre.PostgresImageRepository
import dev.ise.shoppingmap.repository.postgre.PostgresOutfitRepository
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ImageProcesses {
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
        val response: HttpResponse = client.post("$IMAGE_MODULE_URL/rmbg/") {
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
            clothes.add(PostgresClothRepository.getById(it)!!)
        }

        clothes.forEach{
            val image = PostgresImageRepository.getById(it.imageId)!!
            val clothRequest = ClothRequest(it.name, it.link, it.description, it.type, image.bytes)
            clothesRequests.add(clothRequest)
        }

        val response: HttpResponse = client.post("$IMAGE_MODULE_URL/generate_outfit/") {
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
            outfits.add(PostgresOutfitRepository.getById(it)!!)
        }

        outfits.forEach {
            it.clothes.forEach { id ->
                clothes.add(PostgresClothRepository.getById(id)!!)
            }
        }

        clothes.forEach{
            val image = PostgresImageRepository.getById(it.imageId)!!
            val clothRequest = ClothRequest(it.name, it.link, it.description, it.type, image.bytes)
            clothesRequests.add(clothRequest)
        }

        val response: HttpResponse = client.post("$IMAGE_MODULE_URL/generate_capsule/") {
            contentType(ContentType.Application.Json)
            setBody(clothesRequests)
        }

        val generatedImage: Image = Json.decodeFromString(response.bodyAsText())
        return generatedImage.bytes
    }
}