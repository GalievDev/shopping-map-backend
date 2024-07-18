package dev.ise.shoppingmap.routing.v1

import dev.ise.shoppingmap.dao.impl.CapsuleDAOImpl
import dev.ise.shoppingmap.dao.impl.ImageDAOImpl
import dev.ise.shoppingmap.dto.Capsule
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.capsules() {
    route("/capsules") {
        get {
            call.respond(CapsuleDAOImpl.getAll())
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest, "Capsule id must be a number"
            )

            val capsule = CapsuleDAOImpl.getById(id) ?: return@get call.respond(
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

            when (CapsuleDAOImpl.create(
                capsule.name, capsule.description, capsule.outfits, 1
            )) {
                1 -> call.respond(HttpStatusCode.OK, "Capsule created")
                else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(
                HttpStatusCode.BadRequest, "Capsule id must be a number"
            )

            val capsule = CapsuleDAOImpl.getById(id) ?: return@delete call.respond(
                HttpStatusCode.NotFound, "Capsule not found"
            )

            ImageDAOImpl.delete(capsule.image_id)

            when(CapsuleDAOImpl.delete(capsule.id)) {
                1 -> call.respond(HttpStatusCode.OK, "Capsule deleted")
                else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }
    }
}