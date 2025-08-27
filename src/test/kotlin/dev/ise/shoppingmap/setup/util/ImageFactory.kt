package dev.ise.shoppingmap.setup.util

import dev.ise.shoppingmap.dto.Outfit
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.util.*
import kotlinx.coroutines.delay
import java.io.File
import java.nio.file.Path
import javax.imageio.ImageIO

object ImageFactory {

    fun saveImage(name: String, basePath: Path, bytes: String) {
        val fileName = basePath.resolve("$name.png")
        val inputStream = bytes.decodeBase64Bytes().inputStream()
        ImageIO.write(ImageIO.read(inputStream), "png", File(fileName.toString()))
    }

    suspend fun waitNewImage(url: String, id: Int, oldImageId: Int, client: HttpClient): Int {
        repeat(3) {
            val outfit = client.get("$url/$id").body<Outfit>()
            if (outfit.imageId != oldImageId) {
                return outfit.imageId
            }
            delay(1000)
        }
        throw IllegalStateException("Image did not update")
    }
}