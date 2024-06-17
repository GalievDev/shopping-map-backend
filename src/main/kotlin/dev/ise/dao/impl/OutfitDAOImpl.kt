package dev.ise.dao.impl

import dev.ise.dao.OutfitDAO
import dev.ise.dto.Outfit
import dev.ise.mics.Database.query
import dev.ise.mics.Database.update


object OutfitDAOImpl : OutfitDAO {
    override fun create(name: String, description: String, outfitClothIds: List<Int>, image: ByteArray): Int {
        val imageId = ImageDAOImpl.create(name, image)
        if (imageId == -1) return -1
        val outfitId: Int = update("INSERT INTO outfits(name, description, image_id)" +
                " VALUES('$name', '$description', '$imageId') RETURNING id")
        if (outfitId == -1) return -1
        var outfitKeysString = ""
        for (i in 0..<outfitClothIds.size) {
            outfitKeysString += "$outfitId, "
        }
        outfitKeysString += outfitId
        return update("INSERT INTO outfits_clothes ($outfitKeysString) VALUES" +
                " (${outfitClothIds.joinToString(", ")})")
    }

    override fun deleteById(id: Int): Int = update("DELETE FROM outfits WHERE id IN($id)")

    override fun getAll(): List<Outfit> = mutableListOf<Outfit>().apply {
        query(
            "SELECT id, name, description, image_id FROM outfits"
        ) { resultSet ->
            while (resultSet.next()) {
                add(
                    Outfit(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getInt("image_id"),
                    )
                )
            }
        }
    }

    override fun getById(id: Int): Outfit? {
        var returnVal: Outfit? = null
        query("SELECT name, description, image_id FROM outfits WHERE outfits.id IN($id) LIMIT 1") { resultSet ->
            returnVal = Outfit(
                id,
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getInt("image_id"),
            )
        }
        return returnVal
    }
}