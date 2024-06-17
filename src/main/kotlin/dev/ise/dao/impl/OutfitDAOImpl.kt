package dev.ise.dao.impl

import dev.ise.dao.OutfitDAO
import dev.ise.dto.Outfit
import dev.ise.mics.Database.query
import dev.ise.mics.Database.update


object OutfitDAOImpl : OutfitDAO {
    override fun create(name: String, description: String, image: ByteArray): Int {
        val id = ImageDAOImpl.create(name, image)
        return if (id != null) {
            return update("INSERT INTO outfit(name, description, image_id) VALUES('$name', '$description', '$id' )")
        } else {
            -1
        }
    }

    override fun deleteById(id: Int): Int = update("DELETE FROM outfits WHERE id IN($id)")

    override fun getAll(): List<Outfit> = mutableListOf<Outfit>().apply {
        query(
            "SELECT outfits.id, outfits.name, outfits.description, outfits.image_id FROM" +
                    " outfits, clothes, outfits_clothes, images WHERE outfits.image_id = images.id AND" +
                    " outfits_clothes.cloth_id = clothes.id",
        ) { resultSet ->
            val outfitId: Int = resultSet.getInt("id")
            while (resultSet.next()) {
                add(
                    Outfit(
                        outfitId,
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getInt("image_id"),
                        getClothesIds(outfitId)
                    )
                )
            }
        }
    }

    override fun getById(id: Int): Outfit? {
        var returnVal: Outfit? = null
        query("SELECT outfits.name, outfits.description, outfits.image_id FROM outfits WHERE outfits.id IN($id) LIMIT 1") { resultSet ->
            returnVal = Outfit(
                id, resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getInt("image_id"),
                getClothesIds(id)
            )
        }
        return returnVal
    }

    private fun getClothesIds(id: Int): List<Int> {
        return mutableListOf<Int>().apply {
            query("SELECT outfits_clothes.cloth_id FROM outfits_clothes WHERE outfits_clothes.outfit_id IN($id)") { resultSet ->
                while (resultSet.next()) {
                    add(
                        resultSet.getInt("cloth_id")
                    )
                }
            }
        }
    }
}