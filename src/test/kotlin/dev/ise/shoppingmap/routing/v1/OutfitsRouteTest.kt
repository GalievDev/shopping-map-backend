package dev.ise.shoppingmap.routing.v1

import dev.ise.shoppingmap.dto.Cloth
import dev.ise.shoppingmap.dto.ClothType
import dev.ise.shoppingmap.dto.Image
import dev.ise.shoppingmap.dto.Outfit
import dev.ise.shoppingmap.module
import dev.ise.shoppingmap.setup.util.ClothFactory.createCloth
import dev.ise.shoppingmap.setup.util.ClothFactory.createClothes
import dev.ise.shoppingmap.setup.util.ImageFactory.saveImage
import dev.ise.shoppingmap.setup.util.ImageFactory.waitNewImage
import dev.ise.shoppingmap.setup.util.jsonClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.*
import java.nio.file.Paths
import kotlin.test.assertEquals

@DisplayName("Outfits")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class OutfitsRouteTest {

    @BeforeAll
    fun setup() = testApplication {
        application(Application::module)

        val clotTypeToImage: LinkedHashMap<ClothType, MutableList<String>> = linkedMapOf()
        val basePath = Paths.get("src", "test", "resources", "clothes")

        val typeToCount = mapOf(
            ClothType.FOOTWEAR to 3,
            ClothType.UNDERWEAR to 3,
            ClothType.OUTWEAR to 3,
            ClothType.TOP to 3,
            ClothType.ACCESSORY to 7
        )

        typeToCount.forEach { (type, count) ->
            clotTypeToImage[type] = createClothes(type, count, basePath).toMutableList()
        }

        clotTypeToImage.forEach { (type, images) ->
            images.forEach {
                createCloth(jsonClient(), type, it)
            }
        }
        clotTypeToImage.clear()
    }

    @Test
    @Order(0)
    @DisplayName("[POST /api/v1/outfits] - Create outfits")
    fun createOutfit() = testApplication {
        application(Application::module)

        val clothTypeToImage = jsonClient().get("/api/v1/clothes").body<List<Cloth>>()
        val top = clothTypeToImage.filter { it.type == ClothType.TOP }
        val shirt = clothTypeToImage.filter { it.type == ClothType.UNDERWEAR }
        val foot = clothTypeToImage.filter { it.type == ClothType.FOOTWEAR }
        val outwear = clothTypeToImage.filter { it.type == ClothType.OUTWEAR }
        val accessory = clothTypeToImage.filter { it.type == ClothType.ACCESSORY }

        for (it in 1..3) {
            val outfitRequest = Outfit(
                -1,
                "Test outfit $it",
                "Test outfit description $it",
                -1,
                listOf(
                    top.random().id,
                    shirt.random().id,
                    foot.random().id,
                    outwear.random().id,
                    accessory.random().id
                )
            )

            val response = jsonClient().post("/api/v1/outfits") {
                setBody(outfitRequest)
            }

            assertEquals(HttpStatusCode.Created, response.status)
            assertEquals("Outfit created", response.bodyAsText())
        }
    }

    @Test
    @Order(1)
    @DisplayName("[GET /api/v1/outfits] - Get list of outfits")
    fun getOutfits() = testApplication {
        application(Application::module)

        val response = jsonClient().get("/api/v1/outfits")

        assertEquals(HttpStatusCode.OK, response.status)
        response.body<List<Outfit>>().forEach {
            val image = jsonClient().get("/api/v1/images/${it.imageId}").body<Image>()

            val path = Paths.get("src", "test", "resources", "out", "outfits")
            saveImage(it.name, path, image.bytes)

            assertEquals("Test outfit ${it.id}", it.name)
            assertEquals("Test outfit description ${it.id}", it.description)
        }
    }

    @Test
    @Order(2)
    @DisplayName("[GET /api/v1/outfits/{outfitId}] - Get outfit by id")
    fun getOutfit() = testApplication {
        application(Application::module)

        val outfitList = jsonClient().get("/api/v1/outfits").body<List<Outfit>>()

        val response = jsonClient().get("/api/v1/outfits/${outfitList.first().id}")

        val outfit = response.body<Outfit>()
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Test outfit ${outfit.id}", outfit.name)
        assertEquals("Test outfit description ${outfit.id}", outfit.description)
    }

    @Test
    @Order(3)
    @DisplayName("[DELETE /api/v1/outfits/{outfitId}/{clothId}] - Delete cloth from outfit")
    fun deleteClothFromOutfit() = testApplication {
        application(Application::module)

        val outfitList = jsonClient().get("/api/v1/outfits").body<List<Outfit>>()

        outfitList.map {
            val clothId = it.clothes.random()
            val response = jsonClient().delete("/api/v1/outfits/${it.id}/$clothId")

            val newImageId = waitNewImage("/api/v1/outfits/", it.id, it.imageId, jsonClient())
            val image = jsonClient().get("/api/v1/images/$newImageId").body<Image>()
            val path = Paths.get("src", "test", "resources", "out", "outfits")

            saveImage("${it.name} edited", path, image.bytes)

            assertEquals(HttpStatusCode.Accepted, response.status)
            assertEquals("Cloth $clothId deleted from Outfit ${it.name}", response.bodyAsText())
        }
    }

    @Test
    @Order(4)
    @DisplayName("[DELETE /api/v1/outfits/{outfitId}] - Delete outfit")
    fun deleteOutfit() = testApplication {
        application(Application::module)

        val outfitList = jsonClient().get("/api/v1/outfits").body<List<Outfit>>()

        outfitList.forEach {
            val response = jsonClient().delete("/api/v1/outfits/${it.id}")

            assertEquals(HttpStatusCode.Accepted, response.status)
            assertEquals("Outfit deleted", response.bodyAsText())
        }
    }
}