package dev.ise.dao.impl

import dev.ise.dao.OutfitDAO
import dev.ise.dto.Outfit
import dev.ise.mics.Database
import dev.ise.mics.Database.query


object OutfitDAOImpl : OutfitDAO {
    override fun create(name: String, description: String, image: ByteArray) : Int {
        val id = ImageDAOImpl.create(name, image)
        return if (id != null) {
            return Database.update("INSERT INTO outfit(name, description, image_id) VALUES('$name', '$description', '$id' )")
        } else {
            -1
        }
    }

    override fun deleteById(id: Int): Int = Database.update("DELETE FROM outfit WHERE id IN($id)")

    override fun getAll(): List<Outfit> {
        return mutableListOf<Outfit>().apply { query(
            "SELECT outfit.id, outfit.name, outfit.description, outfit.image_id FROM" +
                    " outfit, clothes, outfit_cloth, images WHERE outfit.image_id = images.id AND" +
                    " outfit_cloth.cloth_id = clothes.id",
        ) {resultSet ->
            val outfitId: Int = resultSet.getInt("id")
            while (resultSet.next()) {
                add(Outfit(outfitId,
                    resultSet.getString("name"),
                    resultSet.getString("description"),
                    resultSet.getInt("image_id"),
                    getClothesId(outfitId)))
            }
        }
        }
    }

    override fun getById(id: Int) : Outfit? {
        var returnVal : Outfit? = null
        query("SELECT outfit.name, outfit.description, outfit.image_id FROM outfit WHERE outfit.id = $id LIMIT 1") {
            resultSet ->
            returnVal = Outfit(id, resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getInt("image_id"),
                getClothesId(id)
            )
        }
        return returnVal
    }

    private fun getClothesId(id: Int) : List<Int> {
        return mutableListOf<Int>().apply {
            query("SELECT outfit_cloth.cloth_id FROM outfit_cloth WHERE outfit_cloth.outfit_id = $id") { resultSet ->
                while (resultSet.next()) {
                    add(
                        resultSet.getInt("cloth_id")
                    )
                }
            }
        }
    }
}