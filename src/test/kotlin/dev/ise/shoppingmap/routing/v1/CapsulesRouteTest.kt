package dev.ise.shoppingmap.routing.v1

import dev.ise.shoppingmap.dto.*
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
import java.io.FileOutputStream
import java.nio.file.Paths
import kotlin.test.assertEquals

@DisplayName("Capsules")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CapsulesRouteTest {

    @BeforeAll
    fun setup() = testApplication {
        application(Application::module)

        val clothTypeToImage: LinkedHashMap<ClothType, MutableList<String>> = linkedMapOf()
        val basePath = Paths.get("src", "test", "resources", "clothes")

        val typeToCount = mapOf(
            ClothType.FOOTWEAR to 3,
            ClothType.UNDERWEAR to 3,
            ClothType.OUTWEAR to 3,
            ClothType.TOP to 3,
            ClothType.ACCESSORY to 7
        )

        typeToCount.forEach { (type, count) ->
            clothTypeToImage[type] = createClothes(type, count, basePath).toMutableList()
        }

        clothTypeToImage.forEach { (type, images) ->
            images.forEach {
                createCloth(jsonClient(), type, it)
            }
        }
        clothTypeToImage.clear()

        val clothes = jsonClient().get("/api/v1/clothes").body<List<Cloth>>()
        val top = clothes.filter { it.type == ClothType.TOP }
        val shirt = clothes.filter { it.type == ClothType.UNDERWEAR }
        val foot = clothes.filter { it.type == ClothType.FOOTWEAR }
        val outwear = clothes.filter { it.type == ClothType.OUTWEAR }
        val accessory = clothes.filter { it.type == ClothType.ACCESSORY }

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

            jsonClient().post("/api/v1/outfits") {
                setBody(outfitRequest)
            }
        }
    }

    @Test
    @Order(0)
    @DisplayName("[POST /api/v1/capsules] - Create capsule")
    fun createCapsule() = testApplication {
        application(Application::module)

        val outfits = jsonClient().get("/api/v1/outfits").body<List<Outfit>>()

        val capsule = Capsule(
            -1,
            "Test capsule",
            "Test capsule description",
            -1,
            outfits.map(Outfit::id).toList()
        )

        val response = jsonClient().post("/api/v1/capsules") {
            setBody(capsule)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals("Capsule created", response.bodyAsText())
    }

    @Test
    @Order(1)
    @DisplayName("[GET /api/v1/capsules] - Get list of capsules")
    fun getCapsules() = testApplication {
        application(Application::module)

        val response = jsonClient().get("/api/v1/capsules")

        val capsule = response.body<List<Capsule>>().first()

        val image = jsonClient().get("/api/v1/images/${capsule.imageId}").body<Image>()

        val path = Paths.get("src", "test", "resources", "out", "capsules")
        saveImage(capsule.name, path, image.bytes)

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Test capsule", capsule.name)
        assertEquals("Test capsule description", capsule.description)
    }

    @Test
    @Order(2)
    @DisplayName("[GET /api/v1/capsules/{capsuleId}] - Get capsule by id")
    fun getCapsule() = testApplication {
        application(Application::module)

        val capsuleList = jsonClient().get("/api/v1/capsules").body<List<Outfit>>()

        val response = jsonClient().get("/api/v1/capsules/${capsuleList.first().id}")

        val capsule = response.body<Capsule>()
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Test capsule", capsule.name)
        assertEquals("Test capsule description", capsule.description)
    }

    @Test
    @Order(3)
    @DisplayName("[GET /api/v1/capsules/{capsuleId}/pdf] - Get capsule PDF file")
    fun getCapsulePdf() = testApplication {
        application(Application::module)

        val capsuleList = jsonClient().get("/api/v1/capsules").body<List<Outfit>>()

        val capsuleResponse = jsonClient().get("/api/v1/capsules/${capsuleList.first().id}")
        val pdfResponse = jsonClient().get("/api/v1/capsules/${capsuleList.first().id}/pdf")

        val capsule = capsuleResponse.body<Capsule>()
        val pdfFile = Paths.get("src", "test", "resources", "out", "capsules", "${capsule.name}.pdf").toFile()
        val outputStream = FileOutputStream(pdfFile)
        outputStream.write(pdfResponse.body<ByteArray>())
        outputStream.close()

        assertEquals(HttpStatusCode.OK, capsuleResponse.status)
        assertEquals("Test capsule", capsule.name)
        assertEquals("Test capsule description", capsule.description)
    }

    @Test
    @Order(4)
    @DisplayName("[DELETE /api/v1/capsules/{capsuleId}/{outfitId}] - Delete outfit from capsule")
    fun deleteClothFromOutfit() = testApplication {
        application(Application::module)

        val capsules = jsonClient().get("/api/v1/capsules").body<List<Capsule>>()

        capsules.map {
            val outfitId = it.outfits.random()
            val response = jsonClient().delete("/api/v1/capsules/${it.id}/$outfitId")

            val newImageId = waitNewImage("/api/v1/capsules", it.id, it.imageId, jsonClient())
            val image = jsonClient().get("/api/v1/images/$newImageId").body<Image>()
            val path = Paths.get("src", "test", "resources", "out", "capsules")

            saveImage("${it.name} edited", path, image.bytes)

            assertEquals(HttpStatusCode.Accepted, response.status)
            assertEquals("Outfit $outfitId deleted from Capsule ${it.name}", response.bodyAsText())
        }
    }

    @Test
    @Order(5)
    @DisplayName("[DELETE /api/v1/capsules/{capsuleId}] - Delete capsule")
    fun deleteOutfit() = testApplication {
        application(Application::module)

        val outfitList = jsonClient().get("/api/v1/capsules").body<List<Outfit>>()

        val response = jsonClient().delete("/api/v1/capsules/${outfitList.first().id}")

        assertEquals(HttpStatusCode.Accepted, response.status)
        assertEquals("Capsule deleted", response.bodyAsText())
    }
}