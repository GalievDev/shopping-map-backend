package dev.ise.dao.impl

import dev.ise.dao.ImageDAO
import dev.ise.dto.Image
import dev.ise.mics.Database.query
import dev.ise.mics.Database.update

object ImageDAOImpl: ImageDAO {
    override fun create(name: String, base64: String): Int? {
        update("INSERT INTO images(name, bytes) VALUES ('$name', decode('$base64', 'base64')) RETURNING ID")
        var id: Int? = null
        query("SELECT id FROM images") { resultSet ->
            if (resultSet.next()) {
                id = resultSet.getInt("id")
            }
        }
        return id
    }

    override fun delete(id: Int): Int = update("DELETE FROM images WHERE id IN($id)")

    override fun getById(id: Int): Image? {
        var image: Image? = null

        query("SELECT images.id, images.name, encode(images.bytes, 'base64') AS encoded_bytes FROM images WHERE id IN($id) LIMIT 1") { resultSet ->
            while (resultSet.next()) {
                image = Image(
                    resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("encoded_bytes")
                )
            }
        }
        return image
    }
}