package dev.ise.shoppingmap.setup.util

import dev.ise.shoppingmap.dto.ClothType
import dev.ise.shoppingmap.dto.request.ClothRequest
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.assertEquals

object ClothFactory {
    fun createClothes(type: ClothType, count: Int, basePath: Path): List<String> {
        val path = basePath.resolve(type.name.lowercase())

        return (1..count).map { index ->
            val fileName = "${type.name.lowercase()}_$index.png"
            Files.readAllBytes(Paths.get(path.toString(), fileName)).encodeBase64()
        }
    }

    suspend fun createCloth(client: HttpClient, type: ClothType, image: String) {
        val response = client.post("/api/v1/clothes") {
            setBody(
                ClothRequest(
                    "Test $type",
                    "${type.name.lowercase()}-test.com",
                    "Some test $type description",
                    type,
                    image
                )
            )
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals("Cloth created", response.bodyAsText())
    }
}