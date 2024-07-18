package dev.ise.shoppingmap.routing.v1

import dev.ise.shoppingmap.client.ImageProcesses
import dev.ise.shoppingmap.dao.impl.ClothDAOImpl
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

            ImageDAOImpl.delete(outfit.image_id)

            when(OutfitDAOImpl.delete(outfit.id)) {
                1 -> call.respond(HttpStatusCode.OK, "Outfit deleted")
                else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }

        delete("/{outfitId}/{clothId}") {
            val outfitId = call.parameters["outfitId"]?.toIntOrNull() ?: return@delete call.respond(
                HttpStatusCode.BadRequest, "Outfit id must be a number"
            )

            var outfit = OutfitDAOImpl.getById(outfitId) ?: return@delete call.respond(
                HttpStatusCode.NotFound, "Outfit not found"
            )

            val clothId = call.parameters["clothId"]?.toIntOrNull() ?: return@delete call.respond(
                HttpStatusCode.BadRequest, "Cloth id must be a number"
            )

            val cloth = ClothDAOImpl.getById(clothId) ?: return@delete call.respond(
                HttpStatusCode.NotFound, "Cloth not found"
            )

            if (!outfit.clothes.contains(clothId)) {
                return@delete call.respond(HttpStatusCode.Conflict, "Cloth not found in outfit")
            }

            when(OutfitDAOImpl.deleteCloth(outfit.id, cloth.id)) {
                1 -> call.respond(HttpStatusCode.OK, "Cloth removed from outfit")
                else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }

            val oldImage = outfit.image_id
            outfit = OutfitDAOImpl.getById(outfitId) ?: return@delete call.respond(
                HttpStatusCode.NotFound, "Outfit not found"
            )

            val newImage = ImageProcesses.generateOutfitImage(outfit.clothes)

            val image = ImageDAOImpl.create(outfit.name, newImage)

            OutfitDAOImpl.changeImage(outfit.id, image)
            ImageDAOImpl.delete(oldImage)
        }
    }
}