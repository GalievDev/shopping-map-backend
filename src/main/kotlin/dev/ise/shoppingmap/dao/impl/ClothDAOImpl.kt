package dev.ise.shoppingmap.dao.impl

import dev.ise.shoppingmap.dao.ClothDAO
import dev.ise.shoppingmap.dto.Cloth
import dev.ise.shoppingmap.dto.ClothType
import dev.ise.shoppingmap.dto.Image
import dev.ise.shoppingmap.mics.Database.query
import dev.ise.shoppingmap.mics.Database.update

object ClothDAOImpl: ClothDAO {
    override fun create(name: String, link: String, description: String, clothType: ClothType, image_id: Int): Int {
        return update("INSERT INTO clothes(name, link, description, type, image_id) VALUES('$name', '$link', '$description', '$clothType', '$image_id')")
    }

    override fun delete(id: Int): Int = update("DELETE FROM clothes WHERE id IN($id)")

    override fun getAll(): List<Cloth> = mutableListOf<Cloth>().apply {
        query(
        """
        SELECT 
            clothes.id AS cloth_id, clothes.name AS cloth_name, clothes.link, clothes.description, clothes.type,
            images.id AS image_id, images.name AS image_name, encode(images.bytes, 'base64') AS image_bytes
        FROM clothes 
        JOIN images ON clothes.image_id = images.id
        """
        ) { resultSet ->
            while (resultSet.next()) {
                add(
                    Cloth(
                        resultSet.getInt("cloth_id"), resultSet.getString("cloth_name"),
                        resultSet.getString("link"), resultSet.getString("description"), ClothType.valueOf(resultSet.getString("type")),
                        Image(
                            resultSet.getInt("image_id"), resultSet.getString("image_name"), resultSet.getString("image_bytes")
                        ).id
                    )
                )
            }
        }
    }

    override fun getById(id: Int): Cloth? {
        var cloth: Cloth? = null

        query(
        """
        SELECT 
            clothes.id AS cloth_id, clothes.name AS cloth_name, clothes.link, clothes.description, clothes.type,
            images.id AS image_id, images.name AS image_name, encode(images.bytes, 'base64') AS image_bytes
        FROM clothes 
        JOIN images ON clothes.image_id = images.id 
        WHERE clothes.id IN ($id) 
        LIMIT 1
        """
        ) { resultSet ->
            while (resultSet.next()) {
                cloth = Cloth(
                    resultSet.getInt("cloth_id"), resultSet.getString("cloth_name"),
                    resultSet.getString("link"), resultSet.getString("description"), ClothType.valueOf(resultSet.getString("type")),
                    Image(
                        resultSet.getInt("image_id"), resultSet.getString("image_name"), resultSet.getString("image_bytes")
                    ).id
                )
            }
        }

        return cloth
    }
}