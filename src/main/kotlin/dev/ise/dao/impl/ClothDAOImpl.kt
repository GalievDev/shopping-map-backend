package dev.ise.dao.impl

import dev.ise.dao.ClothDAO
import dev.ise.dto.Cloth
import dev.ise.dto.ClothType
import dev.ise.dto.Image
import dev.ise.mics.Database.query
import dev.ise.mics.Database.update

object ClothDAOImpl: ClothDAO {
    override fun create(name: String, link: String, description: String, clothType: ClothType, image_id: Int): Int {
        return update("INSERT INTO clothes(name, link, description, type, image_id) VALUES('$name', '$link', '$description', '$clothType', '$image_id')")
    }

    override fun delete(id: Int): Int = update("DELETE FROM clothes WHERE id IN($id)")

    override fun getAll(): List<Cloth> = mutableListOf<Cloth>().apply {
        query("SELECT *, images.id, images.name, images.bytes FROM clothes JOIN images ON clothes.image_id = images.id") { resultSet ->
            while (resultSet.next()) {
                add(
                    Cloth(
                        resultSet.getInt("id"), resultSet.getString("name"),
                        resultSet.getString("link"), resultSet.getString("description"), ClothType.valueOf(resultSet.getString("type")),
                        Image(
                            resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("bytes")
                        ).id
                    )
                )
            }
        }
    }

    override fun getById(id: Int): Cloth? {
        var cloth: Cloth? = null

        query("SELECT *, images.bytes FROM clothes JOIN images ON clothes.image_id = images.id WHERE clothes.id IN($id) LIMIT 1") { resultSet ->
            while (resultSet.next()) {
                cloth = Cloth(
                    resultSet.getInt("id"), resultSet.getString("name"),
                    resultSet.getString("link"), resultSet.getString("description"), ClothType.valueOf(resultSet.getString("type")),
                    Image(
                        resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("bytes")
                    ).id
                )
            }
        }

        return cloth
    }
}