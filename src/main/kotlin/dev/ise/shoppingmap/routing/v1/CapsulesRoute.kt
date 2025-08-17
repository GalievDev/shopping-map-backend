package dev.ise.shoppingmap.routing.v1

import dev.ise.shoppingmap.client.ImageProcesses
import dev.ise.shoppingmap.dto.Capsule
import dev.ise.shoppingmap.dto.Image
import dev.ise.shoppingmap.mics.SUCCESS
import dev.ise.shoppingmap.repository.postgre.PostgresCapsuleRepository
import dev.ise.shoppingmap.repository.postgre.PostgresImageRepository
import dev.ise.shoppingmap.repository.postgre.PostgresOutfitRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.capsules() {
    route("/capsules") {
        get {
            call.respond(PostgresCapsuleRepository.getAll())
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest, "Capsule id must be a number"
            )

            val capsule = PostgresCapsuleRepository.getById(id) ?: return@get call.respond(
                HttpStatusCode.NotFound, "Capsule not found"
            )

            call.respond(capsule)
        }
        post {
            val capsule = call.receive<Capsule>()

            if (capsule.name.isBlank()) return@post call.respond(
                HttpStatusCode.BadRequest, "Name cannot be blank"
            )

            if (capsule.outfits.isEmpty()) return@post call.respond(
                HttpStatusCode.BadRequest, "Outfits ids cannot be blank"
            )

            val generatedImage = ImageProcesses.generateCapsuleImage(capsule.outfits)

            val image = PostgresImageRepository.create(
                Image(capsule.name, generatedImage)
            )

            when (PostgresCapsuleRepository.create(
                Capsule(-1, capsule.name, capsule.description, image, capsule.outfits)
            )) {
                SUCCESS -> call.respond(HttpStatusCode.OK, "Capsule created")
                else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(
                HttpStatusCode.BadRequest, "Capsule id must be a number"
            )

            val capsule = PostgresCapsuleRepository.getById(id) ?: return@delete call.respond(
                HttpStatusCode.NotFound, "Capsule not found"
            )

            PostgresImageRepository.delete(capsule.imageId)

            when(PostgresCapsuleRepository.delete(id)) {
                SUCCESS -> call.respond(HttpStatusCode.OK, "Capsule deleted")
                else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }

        delete("/{capsuleId}/{outfitId}") {
            val capsuleId = call.parameters["capsuleId"]?.toIntOrNull() ?: return@delete call.respond(
                HttpStatusCode.BadRequest, "Capsule id must be a number"
            )

            var capsule = PostgresCapsuleRepository.getById(capsuleId) ?: return@delete call.respond(
                HttpStatusCode.NotFound, "Capsule not found"
            )

            val outfitId = call.parameters["outfitId"]?.toIntOrNull() ?: return@delete call.respond(
                HttpStatusCode.BadRequest, "Outfit id must be a number"
            )

            PostgresOutfitRepository.getById(outfitId) ?: return@delete call.respond(
                HttpStatusCode.NotFound, "Outfit not found"
            )

            if (!capsule.outfits.contains(outfitId)) {
                return@delete call.respond(HttpStatusCode.Conflict, "Outfit not found in capsule")
            }

            when(PostgresCapsuleRepository.deleteOutfit(capsuleId, outfitId)) {
                SUCCESS -> call.respond(HttpStatusCode.OK, "Outfit removed from capsule")
                else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }
            val oldImage = capsule.imageId
            capsule = PostgresCapsuleRepository.getById(capsuleId) ?: return@delete call.respond(
                HttpStatusCode.NotFound, "Capsule not found"
            )

            val newImage = ImageProcesses.generateCapsuleImage(capsule.outfits)

            val image = PostgresImageRepository.create(Image(capsule.name, newImage))

            PostgresCapsuleRepository.changeImage(capsuleId, image)
            PostgresImageRepository.delete(oldImage)
        }
    }
}