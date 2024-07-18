package dev.ise.shoppingmap.routing.v1

import dev.ise.shoppingmap.client.ImageProcesses
import dev.ise.shoppingmap.dao.impl.ClothDAOImpl
import dev.ise.shoppingmap.dao.impl.ImageDAOImpl
import dev.ise.shoppingmap.dto.Image
import dev.ise.shoppingmap.request.ClothRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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

            val rembgImg = ImageProcesses.remImgBg(Image(1, cloth.name, cloth.image))

            val image = ImageDAOImpl.create(
                cloth.name, rembgImg
            )

            when(ClothDAOImpl.create(
                cloth.name, cloth.link, cloth.description, cloth.type, image
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

            ImageDAOImpl.delete(cloth.image_id)

            when(ClothDAOImpl.delete(cloth.id)) {
                1 -> call.respond(HttpStatusCode.OK, "Cloth deleted")
                else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }
    }
}