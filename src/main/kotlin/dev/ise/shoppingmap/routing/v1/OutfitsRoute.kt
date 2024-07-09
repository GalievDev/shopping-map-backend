package dev.ise.shoppingmap.routing.v1

import dev.ise.shoppingmap.client.ImageProcesses
import dev.ise.shoppingmap.dao.impl.ImageDAOImpl
import dev.ise.shoppingmap.dao.impl.OutfitDAOImpl
import dev.ise.shoppingmap.dto.Outfit
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.outfits() {
    route("/outfits") {
        get {
            call.respond(OutfitDAOImpl.getAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest, "Outfit id must be a number"
            )

            val outfit = OutfitDAOImpl.getById(id) ?: return@get call.respond(
                HttpStatusCode.NotFound, "Outfit not found"
            )

            call.respond(outfit)
        }

        post {
            val outfit = call.receive<Outfit>()

            if (outfit.name.isBlank()) return@post call.respond(
                HttpStatusCode.BadRequest, "Name cannot be blank"
            )

            if (outfit.clothes.isEmpty()) return@post call.respond(
                HttpStatusCode.BadRequest, "Clothes ids cannot be blank"
            )

            val generatedImage = ImageProcesses.generateOutfitImage(outfit.clothes)

            val image = ImageDAOImpl.create(
                outfit.name, generatedImage
            )

            when (OutfitDAOImpl.create(
                outfit.name, outfit.description, outfit.clothes, image
            )) {
                1 -> call.respond(HttpStatusCode.OK, "Outfit created")
                else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(
                HttpStatusCode.BadRequest, "Outfit id must be a number"
            )

            val outfit = OutfitDAOImpl.getById(id) ?: return@delete call.respond(
                HttpStatusCode.NotFound, "Outfit not found"
            )

            when(OutfitDAOImpl.delete(outfit.id)) {
                1 -> call.respond(HttpStatusCode.OK, "Outfit deleted")
                else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }
    }
}