package dev.ise.shoppingmap.dao.impl

import dev.ise.shoppingmap.dao.CapsuleDAO
import dev.ise.shoppingmap.dto.Capsule
import dev.ise.shoppingmap.mics.Database.query
import dev.ise.shoppingmap.mics.Database.update
import dev.ise.shoppingmap.mics.Database.updateWithId

object CapsuleDAOImpl: CapsuleDAO {
    override fun create(name: String, description: String, outfits: List<Int>, imageId: Int): Int {
        val capsuleId: Int = updateWithId(
            "INSERT INTO capsules(name, description, image_id) VALUES ('$name', '$description', '$imageId') RETURNING id"
        )
        if (capsuleId == -1) return -1

        var executionCode: Int = -1
        outfits.forEach { id ->
            executionCode = update("INSERT INTO capsules_outfits(capsule_id, outfit_id) VALUES ('$capsuleId', '$id')")
        }
        return executionCode
    }

    override fun delete(id: Int): Int = update("DELETE FROM outfits WHERE id IN($id)")

    override fun deleteOutfit(capsuleId: Int, outfitId: Int): Int = update("DELETE FROM capsules_outfits WHERE capsule_id IN($capsuleId) AND outfit_id IN($outfitId)")

    override fun changeImage(capsuleId: Int, imageId: Int): Int {
        return update("UPDATE capsules SET image_id = $imageId WHERE id IN($capsuleId)")
    }

    override fun getAll(): List<Capsule> = mutableListOf<Capsule>().apply {
        query("""
                SELECT o.id, o.name, o.description, o.image_id, ARRAY_AGG(oc.outfit_id) AS outfit_ids FROM capsules o 
                LEFT JOIN capsules_outfits oc ON o.id = oc.capsule_id 
                GROUP BY o.id
                """
        ) { resultSet ->
            while (resultSet.next()) {
                val outfits: Array<Int> = resultSet.getArray("outfit_ids").array as Array<Int>
                add(
                    Capsule(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getInt("image_id"),
                        outfits.toList()
                    )
                )
            }
        }
    }

    override fun getById(id: Int): Capsule? {
        var returnVal: Capsule? = null
        query(
            """
            SELECT o.id, o.name, o.description, o.image_id, ARRAY_AGG(oc.outfit_id) AS outfit_ids FROM capsules o 
            LEFT JOIN capsules_outfits oc ON o.id = oc.capsule_id
            WHERE o.id IN($id) GROUP BY o.id LIMIT 1
            """
        ) { resultSet ->
            if (resultSet.next()) {
                val outfits: Array<Int> = resultSet.getArray("outfit_ids").array as Array<Int>
                returnVal = Capsule(
                    id,
                    resultSet.getString("name"),
                    resultSet.getString("description"),
                    resultSet.getInt("image_id"),
                    outfits.toList()
                )
            }
        }
        return returnVal
    }
}