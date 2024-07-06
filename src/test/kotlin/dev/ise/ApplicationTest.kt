package dev.ise

import dev.ise.shoppingmap.dto.Capsule
import dev.ise.shoppingmap.dto.ClothType
import dev.ise.shoppingmap.dto.Outfit
import dev.ise.shoppingmap.plugins.configureRouting
import dev.ise.shoppingmap.request.ClothRequest
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation

const val TEST_IMAGE_BYTE_CODE = "iVBORw0KGgoAAAANSUhEUgAAA/gAAAKmCAYAAAD5KDWkAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAALEgAACxIB0t1+/AAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTNui8sowAAAAVdEVYdENyZWF0aW9uIFRpbWUAMTYvMS8xNjG1ie8AAB9+SURBVHic7d3rUtvIuoDhHhxj8EBYrFC5/wtbN5AUwdiSFQtFPfvHDLPnQBIOtlv69Dw/p2rIV0Agr7rV/cv//ve/3xIAAAAwaielBwAAAADeTuADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEIDABwAAgAAEPgAAAAQg8AEAACAAgQ8AAAABCHwAAAAIQOADAABAAAIfAAAAAhD4AAAAEMC70gMAAETQ93369u1bats29X2f+r5P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BD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAIQ+AAAABCAwAcAAIAABD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAIQ+AAAABCAwAcAAIAABD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAIQ+AAAABCAwAcAAIAABD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAIQ+AAAABCAwAcAAIAABD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAIQ+AAAABCAwAcAAIAABD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAIQ+AAAABCAwAcAAIAABD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAIQ+AAAABCAwAcAAIAABD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAIQ+AAAABCAwAcAAIAABD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAIQ+AAAABCAwAcAAIAABD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAIQ+AAAABCAwAcAAIAABD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAIQ+AAAABCAwAcAAIAABD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAIQ+AAAABCAwAcAAIAABD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAIQ+AAAABCAwAcAAIAABD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAIQ+AAAABCAwAcAAIAABD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAIQ+AAAABCAwAcAAIAABD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAIQ+AAAABCAwAcAAIAABD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAIQ+AAAABCAwAcAAIAABD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAIQ+AAAABCAwAcAAIAABD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAIQ+AAAABCAwAcAAIAABD4AAAAEIPABAAAgAIEPAAAAAQh8AAAACEDgAwAAQAACHwAAAAL4P5r8eli1KhNuAAAAAElFTkSuQmCC"

class ApplicationTest {
    @Test
    fun testClothesGetAll() = testApplication {
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
            println(status)
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testClothesGetById() = testApplication {
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
        client.get("/api/v1/clothes/1").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testClothesPost() = testApplication {
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
        createClient {
            install(ClientContentNegotiation) {
                json(
                    contentType = ContentType.Application.Json,
                    json = Json {
                        prettyPrint = true
                        ignoreUnknownKeys = true
                    }
                )
            }
        }.post("/api/v1/clothes") {
            contentType(ContentType.Application.Json)
            setBody(
                ClothRequest("TEST_CLOTH", "TEST_LINK", "TEST_DESCRIPTION",
                ClothType.NONE, TEST_IMAGE_BYTE_CODE)
            )
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

//    @Test
//    fun testClothesDeleteById() {
//        TODO("Lack of backend functionality")
//
//    }

    @Test
    fun testOutfitsGetAll() = testApplication {
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
        client.get("/api/v1/outfits").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testOutfitsGetById() = testApplication {
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
        client.get("/api/v1/outfits/1").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testOutfitsPost() = testApplication {
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
        createClient {
            install(ClientContentNegotiation) {
                json(
                    contentType = ContentType.Application.Json,
                    json = Json {
                        prettyPrint = true
                        ignoreUnknownKeys = true
                    }
                )
            }
        }.post("/api/v1/outfits") {
            contentType(ContentType.Application.Json)
            setBody(
                Outfit(name = "TEST_OUTFIT", description = "TEST_DESCRIPTION", image_id = 1, clothes = listOf(2, 3))
            )
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

//    @Test
//    fun testOutfitsDeleteDyId() {
//        TODO("Lack of backend functionality")
//    }

    @Test
    fun testCapsulesGetAll() = testApplication {
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
        client.get("/api/v1/capsules").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testCapsulesGetById() = testApplication {
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
        client.get("/api/v1/capsules/1").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testCapsulesPost() = testApplication {
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
        createClient {
            install(ClientContentNegotiation) {
                json(
                    contentType = ContentType.Application.Json,
                    json = Json {
                        prettyPrint = true
                        ignoreUnknownKeys = true
                    }
                )
            }
        }.post("/api/v1/capsules") {
            contentType(ContentType.Application.Json)
            setBody(
                Capsule(name = "TEST_CAPSULE", description = "TEST_DESCRIPTION", image_id = 1, outfits = listOf(1))
            )
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

//    @Test
//    fun testCapsulesDeleteById() {
//        TODO("Lack of backend functionality")
//    }
}
