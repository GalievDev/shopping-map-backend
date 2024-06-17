package dev.ise.dao.impl

import dev.ise.dao.ImageDAO
import dev.ise.dto.Image
import dev.ise.mics.Database.query
import dev.ise.mics.Database.update

object ImageDAOImpl: ImageDAO {
    override fun create(name: String, bytes: ByteArray): Int {
        return update("INSERT INTO images(name, bytes) VALUES ('$name', '$bytes') RETURNING ID")
    }

    override fun delete(id: Int): Int = update("DELETE FROM images WHERE id IN($id)")

    override fun getById(id: Int): Image? {
        var image: Image? = null

        query("SELECT * FROM images WHERE id IN($id) LIMIT 1") { resultSet ->
            while (resultSet.next()) {
                image = Image(
                    resultSet.getInt("id"), resultSet.getString("name"), resultSet.getBytes("bytes")
                )
            }
        }
        return image
    }
}