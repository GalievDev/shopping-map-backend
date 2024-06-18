package dev.ise.routing.v1

import dev.ise.dao.impl.ClothDAOImpl
import dev.ise.request.ClothRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun Route.clothes() {
    route("/clothes") {
        get {
            call.respond(ClothDAOImpl.getAll())
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest, "Cloth id must be a number"
            )

            val cloth = ClothDAOImpl.getById(id) ?: return@get call.respond(
                HttpStatusCode.NotFound, "Cloth not found"
            )

            call.respond(cloth)
        }
        post {
            val cloth = call.receive<ClothRequest>()

            if (cloth.name.isBlank()) return@post call.respond(
                HttpStatusCode.BadRequest, "Name cannot be blank"
            )

            when(ClothDAOImpl.create(
                cloth.name, cloth.link, cloth.description, cloth.type, cloth.image
            )) {
                1 -> call.respond(HttpStatusCode.OK, "Cloth created")
                else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }
        delete("{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(
                HttpStatusCode.BadRequest, "Cloth id must be a number"
            )

            val cloth = ClothDAOImpl.getById(id) ?: return@delete call.respond(
                HttpStatusCode.NotFound, "Cloth not found"
            )

            when(ClothDAOImpl.deleteById(cloth.id)) {
                1 -> call.respond(HttpStatusCode.OK, "Cloth deleted")
                else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }
    }
}