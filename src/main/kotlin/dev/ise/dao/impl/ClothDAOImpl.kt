package dev.ise.dao.impl

import dev.ise.dao.ClothDAO
import dev.ise.dto.Cloth
import dev.ise.dto.Image
import dev.ise.mics.Database.query
import dev.ise.mics.Database.update

object ClothDAOImpl: ClothDAO {
    override fun create(name: String, link: String, image: ByteArray): Int {
        val statement = ImageDAOImpl.create(name, image)
        val keys = statement?.generatedKeys
        if (keys?.next()!!) {
            update("INSERT INTO clothes(name, link, image) VALUES('$name', '$link', '${keys.getInt(1)}')")
        }
        return -1
    }

    override fun deleteById(id: Int): Int {
        update("DELETE FROM clothes WHERE id IN($id)")
        return -1
    }

    override fun getAll(): List<Cloth> = mutableListOf<Cloth>().apply {
        query("SELECT clothes.id, clothes.name, clothes.image_id, images.id, images.name, images.byteArray FROM clothes JOIN images ON clothes.image_id = images.id") { resultSet ->
            while (resultSet.next()) {
                add(
                    Cloth(
                        resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("link"), Image(
                            resultSet.getInt("id"), resultSet.getString("name"), resultSet.getBytes("byteArray")
                        ).id
                    )
                )
            }
        }
    }

    override fun getById(id: Int): Cloth? {
        var cloth: Cloth? = null

        query("SELECT clothes.id, clothes.name, clothes.link, images.byteArray FROM clothes JOIN images ON clothes.image_id = images.id WHERE clothes.id IN($id) LIMIT 1") { resultSet ->
            while (resultSet.next()) {
                cloth = Cloth(
                    resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("link"), Image(
                        resultSet.getInt("id"), resultSet.getString("name"), resultSet.getBytes("byteArray")
                    ).id
                )
            }
        }

        return cloth
    }
}