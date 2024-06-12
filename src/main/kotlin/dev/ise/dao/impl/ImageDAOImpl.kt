package dev.ise.dao.impl

import dev.ise.dao.ImageDAO
import dev.ise.dto.Image
import dev.ise.mics.Database.query
import dev.ise.mics.Database.update
import java.sql.Statement

object ImageDAOImpl: ImageDAO {
    override fun create(name: String, byteArray: ByteArray): Statement? =
        update("INSERT INTO images(name, byteArray) VALUES ('$name', $byteArray)")

    override fun delete(id: Int): Int {
        TODO("Not yet implemented")
    }

    override fun getById(id: Int): Image? {
        var image: Image? = null

        query("SELECT images.id, images.name, images.byteArray ON images.id = id WHERE images.id IN($id) LIMIT 1") { resultSet ->
            while (resultSet.next()) {
                image = Image(
                    resultSet.getInt("id"), resultSet.getString("name"), resultSet.getBytes("byteArray")
                )
            }
        }
        return image
    }
}