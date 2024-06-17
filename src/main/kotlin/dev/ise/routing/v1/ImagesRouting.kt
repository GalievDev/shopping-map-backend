package dev.ise.routing.v1

import dev.ise.dao.impl.ImageDAOImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.images() {
    route("/images") {
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest, "Image id must be a number"
            )

            val cloth = ImageDAOImpl.getById(id) ?: return@get call.respond(
                HttpStatusCode.NotFound, "Image not found"
            )

            call.respond(cloth)
        }
    }
}