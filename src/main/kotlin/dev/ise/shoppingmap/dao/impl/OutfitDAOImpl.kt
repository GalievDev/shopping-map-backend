package dev.ise.shoppingmap.dao.impl

import dev.ise.shoppingmap.dao.OutfitDAO
import dev.ise.shoppingmap.dto.Outfit
import dev.ise.shoppingmap.mics.Database.query
import dev.ise.shoppingmap.mics.Database.update
import dev.ise.shoppingmap.mics.Database.updateWithId


object OutfitDAOImpl : OutfitDAO {
    override fun create(name: String, description: String, clothes: List<Int>, imageId: Int): Int {
        val outfitId: Int = updateWithId(
            "INSERT INTO outfits(name, description, image_id) VALUES ('$name', '$description', '$imageId') RETURNING id"
        )
        if (outfitId == -1) return -1

        clothes.forEach { id ->
            return update("INSERT INTO outfits_clothes(outfit_id, cloth_id) VALUES ('$outfitId', '$id')")
        }
        return -1
    }

    override fun delete(id: Int): Int = update("DELETE FROM outfits WHERE id IN($id)")

    override fun getAll(): List<Outfit> = mutableListOf<Outfit>().apply {
        query("""
                SELECT o.id, o.name, o.description, o.image_id, ARRAY_AGG(oc.cloth_id) AS cloth_ids FROM outfits o 
                LEFT JOIN outfits_clothes oc ON o.id = oc.outfit_id 
                GROUP BY o.id
                """
        ) { resultSet ->
            while (resultSet.next()) {
                val clothes: Array<Int> = resultSet.getArray("cloth_ids").array as Array<Int>
                add(
                    Outfit(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getInt("image_id"),
                        clothes.toList()
                    )
                )
            }
        }
    }

    override fun getById(id: Int): Outfit? {
        var returnVal: Outfit? = null
        query(
            """
            SELECT o.id, o.name, o.description, o.image_id, ARRAY_AGG(oc.cloth_id) AS cloth_ids FROM outfits o 
            LEFT JOIN outfits_clothes oc ON o.id = oc.outfit_id
            WHERE o.id IN($id) GROUP BY o.id LIMIT 1
            """
        ) { resultSet ->
            if (resultSet.next()) {
                val clothes: Array<Int> = resultSet.getArray("cloth_ids").array as Array<Int>
                returnVal = Outfit(
                    id,
                    resultSet.getString("name"),
                    resultSet.getString("description"),
                    resultSet.getInt("image_id"),
                    clothes.toList()
                )
            }
        }
        return returnVal
    }
}