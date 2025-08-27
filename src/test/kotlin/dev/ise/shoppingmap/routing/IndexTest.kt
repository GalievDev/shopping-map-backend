package dev.ise.shoppingmap.routing

import dev.ise.shoppingmap.module
import dev.ise.shoppingmap.setup.SetupTest
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.*
import kotlin.test.Test
import kotlin.test.assertEquals

@DisplayName("Index")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class IndexTest : SetupTest() {
    @Test
    @Order(0)
    @DisplayName("[GET /] - Verify index existence")
    fun initialize() = testApplication {
        application(Application::module)

        val response = client.get("/")

        assertEquals(HttpStatusCode.OK, response.status)
        assert(response.bodyAsText().isNotBlank())
    }
}