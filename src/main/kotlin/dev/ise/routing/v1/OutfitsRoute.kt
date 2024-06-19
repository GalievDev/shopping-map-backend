package dev.ise.routing.v1

import dev.ise.dao.impl.OutfitDAOImpl
import dev.ise.dto.Outfit
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

            when (OutfitDAOImpl.create(
                outfit.name, outfit.description, outfit.clothes, "dsfsdfsd"
            )) {
                1 -> call.respond(HttpStatusCode.OK, "Outfit created")
                else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }
    }
}