package dev.ise.shoppingmap.routing.v1

import dev.ise.shoppingmap.client.ImageProcesses
import dev.ise.shoppingmap.dto.Cloth
import dev.ise.shoppingmap.dto.Image
import dev.ise.shoppingmap.dto.request.ClothRequest
import dev.ise.shoppingmap.mics.SUCCESS
import dev.ise.shoppingmap.repository.postgre.PostgresClothRepository
import dev.ise.shoppingmap.repository.postgre.PostgresImageRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.clothes() {
    route("/clothes") {
        get {
            call.respond(PostgresClothRepository.getAll())
        }
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest, "Cloth id must be a number"
            )

            val cloth = PostgresClothRepository.getById(id) ?: return@get call.respond(
                HttpStatusCode.NotFound, "Cloth not found"
            )

            call.respond(cloth)
        }
        post {
            val cloth = call.receive<ClothRequest>()

            if (cloth.name.isBlank()) return@post call.respond(
                HttpStatusCode.BadRequest, "Name cannot be blank"
            )

            val rembgImg = ImageProcesses.remImgBg(Image(cloth.name, cloth.image))

            val image = PostgresImageRepository.create(
                Image(cloth.name, rembgImg)
            )

            when(PostgresClothRepository.create(
                Cloth(-1, cloth.name, cloth.link, cloth.description, cloth.type, image)
            )) {
                SUCCESS -> call.respond(HttpStatusCode.OK, "Cloth created")
                else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }
        delete("{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(
                HttpStatusCode.BadRequest, "Cloth id must be a number"
            )

            val cloth = PostgresClothRepository.getById(id) ?: return@delete call.respond(
                HttpStatusCode.NotFound, "Cloth not found"
            )

            PostgresImageRepository.delete(cloth.imageId)

            when(PostgresClothRepository.delete(id)) {
                SUCCESS -> call.respond(HttpStatusCode.OK, "Cloth deleted")
                else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }
    }
}