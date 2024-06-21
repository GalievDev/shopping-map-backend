package dev.ise.shoppingmap.dao.impl

import dev.ise.shoppingmap.dao.ImageDAO
import dev.ise.shoppingmap.dto.Image
import dev.ise.shoppingmap.mics.Database.query
import dev.ise.shoppingmap.mics.Database.update
import dev.ise.shoppingmap.mics.Database.updateWithId

object ImageDAOImpl: ImageDAO {
    override fun create(name: String, base64: String): Int {
        return updateWithId("INSERT INTO images(name, bytes) VALUES ('$name', decode('$base64', 'base64')) RETURNING id")
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