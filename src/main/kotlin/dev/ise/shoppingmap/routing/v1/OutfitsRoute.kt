package dev.ise.shoppingmap.routing.v1

import dev.ise.shoppingmap.client.ImageProcesses
import dev.ise.shoppingmap.dto.Image
import dev.ise.shoppingmap.dto.Outfit
import dev.ise.shoppingmap.mics.SUCCESS
import dev.ise.shoppingmap.repository.postgre.PostgresClothRepository
import dev.ise.shoppingmap.repository.postgre.PostgresImageRepository
import dev.ise.shoppingmap.repository.postgre.PostgresOutfitRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.outfits() {
    route("/outfits") {
        get {
            call.respond(PostgresOutfitRepository.getAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest, "Outfit id must be a number"
            )

            val outfit = PostgresOutfitRepository.getById(id) ?: return@get call.respond(
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

            val image = PostgresImageRepository.create(
                Image(outfit.name, generatedImage)
            )

            when (PostgresOutfitRepository.create(
                Outfit(-1, outfit.name, outfit.description, image, outfit.clothes)
            )) {
                SUCCESS -> call.respond(HttpStatusCode.OK, "Outfit created")
                else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(
                HttpStatusCode.BadRequest, "Outfit id must be a number"
            )

            val outfit = PostgresOutfitRepository.getById(id) ?: return@delete call.respond(
                HttpStatusCode.NotFound, "Outfit not found"
            )

            PostgresImageRepository.delete(outfit.imageId)

            when(PostgresOutfitRepository.delete(id)) {
                SUCCESS -> call.respond(HttpStatusCode.OK, "Outfit deleted")
                else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }

        delete("/{outfitId}/{clothId}") {
            val outfitId = call.parameters["outfitId"]?.toIntOrNull() ?: return@delete call.respond(
                HttpStatusCode.BadRequest, "Outfit id must be a number"
            )

            var outfit = PostgresOutfitRepository.getById(outfitId) ?: return@delete call.respond(
                HttpStatusCode.NotFound, "Outfit not found"
            )

            val clothId = call.parameters["clothId"]?.toIntOrNull() ?: return@delete call.respond(
                HttpStatusCode.BadRequest, "Cloth id must be a number"
            )

            PostgresClothRepository.getById(clothId) ?: return@delete call.respond(
                HttpStatusCode.NotFound, "Cloth not found"
            )

            if (!outfit.clothes.contains(clothId)) {
                return@delete call.respond(HttpStatusCode.Conflict, "Cloth not found in outfit")
            }

            when(PostgresOutfitRepository.deleteCloth(outfitId, clothId)) {
                SUCCESS -> call.respond(HttpStatusCode.OK, "Cloth removed from outfit")
                else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }

            val oldImage = outfit.imageId
            outfit = PostgresOutfitRepository.getById(outfitId) ?: return@delete call.respond(
                HttpStatusCode.NotFound, "Outfit not found"
            )

            val newImage = ImageProcesses.generateOutfitImage(outfit.clothes)

            val image = PostgresImageRepository.create(Image(outfit.name, newImage))

            PostgresOutfitRepository.changeImage(outfitId, image)
            PostgresImageRepository.delete(oldImage)
        }
    }
}