package dev.ise.shoppingmap.routing.v1

import dev.ise.shoppingmap.dto.Cloth
import dev.ise.shoppingmap.dto.ClothType
import dev.ise.shoppingmap.module
import dev.ise.shoppingmap.setup.util.ClothFactory.createCloth
import dev.ise.shoppingmap.setup.util.ClothFactory.createClothes
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

@DisplayName("Clothes")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ClothesRouteTest {

    private val clothTypeToImage: MutableMap<ClothType, MutableList<String>> = mutableMapOf()

    @BeforeAll
    fun setup() = testApplication {
        application(Application::module)

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
    }


    @Test
    @Order(0)
    @DisplayName("[POST /api/v1/clothes] - Create cloth")
    fun createCloth() = testApplication {
        application(Application::module)

        clothTypeToImage.forEach { (type, images) ->
            images.forEach {
                createCloth(jsonClient(), type, it)
            }
        }

        clothTypeToImage.clear()
    }

    @Test
    @Order(1)
    @DisplayName("[GET /api/v1/clothes] - Get list of cloth")
    fun getClothes() = testApplication {
        application(Application::module)

        val response = jsonClient().get("/api/v1/clothes")

        assertEquals(HttpStatusCode.OK, response.status)
        response.body<List<Cloth>>().forEach {
            assertEquals("Test ${it.type}", it.name)
            assertEquals("${it.type.name.lowercase()}-test.com", it.link)
            assertEquals("Some test ${it.type} description", it.description)
        }
    }

    @Test
    @Order(2)
    @DisplayName("[GET /api/v1/clothes/{clothId}] - Get cloth by id")
    fun getCloth() = testApplication {
        application(Application::module)

        val clothList = jsonClient().get("/api/v1/clothes").body<List<Cloth>>()

        val response = jsonClient().get("/api/v1/clothes/${clothList.first().id}")

        val cloth = response.body<Cloth>()
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Test ${cloth.type}", cloth.name)
        assertEquals("${cloth.type.name.lowercase()}-test.com", cloth.link)
        assertEquals("Some test ${cloth.type} description", cloth.description)
    }

    @Test
    @Order(3)
    @DisplayName("[DELETE /api/v1/clothes/{clothId}] - Delete cloth")
    fun deleteCloth() = testApplication {
        application(Application::module)

        val clothList = jsonClient().get("/api/v1/clothes").body<List<Cloth>>()

        clothList.forEach {
            val response = jsonClient().delete("/api/v1/clothes/${it.id}")

            assertEquals(HttpStatusCode.Accepted, response.status)
            assertEquals("Cloth deleted", response.bodyAsText())
        }
    }


}