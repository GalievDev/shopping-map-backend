package dev.ise.shoppingmap.repository.postgre

import dev.ise.shoppingmap.dto.Outfit
import dev.ise.shoppingmap.mics.FAILURE
import dev.ise.shoppingmap.mics.SUCCESS
import dev.ise.shoppingmap.mics.dbQuery
import dev.ise.shoppingmap.repository.OutfitRepository
import dev.ise.shoppingmap.table.OutfitTable
import dev.ise.shoppingmap.table.relation.OutfitsClothes
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object PostgresOutfitRepository: OutfitRepository {
    override suspend fun create(outfit: Outfit): Int = dbQuery {
        val insertOutfit = OutfitTable.insert {
            it[name] = outfit.name
            it[description] = outfit.description
            it[imageId] = outfit.imageId
        }
        outfit.clothes.forEach { id ->
            OutfitsClothes.insert {
                it[outfitId] = insertOutfit[OutfitTable.id].value
                it[clothId] = id
            }
        }
        if (insertOutfit.insertedCount > 0) SUCCESS else FAILURE
    }

    override suspend fun delete(id: Int): Int = dbQuery {
        val deleteResult = OutfitTable.deleteWhere { OutfitTable.id eq id }
        if (deleteResult > 0) SUCCESS else FAILURE
    }

    override suspend fun deleteCloth(outfitId: Int, clothId: Int): Int = dbQuery {
        val deleteResult = OutfitsClothes
            .deleteWhere { OutfitsClothes.outfitId eq outfitId and(OutfitsClothes.clothId eq clothId) }

        if (deleteResult > 0) SUCCESS else FAILURE
    }

    override suspend fun changeImage(outfitId: Int, newImageId: Int): Int = dbQuery {
        val updateResult = OutfitTable.update(where = { OutfitTable.id eq outfitId }) {
            it[imageId] = newImageId
        }
        if (updateResult > 0) SUCCESS else FAILURE
    }

    override suspend fun getAll(): List<Outfit> = dbQuery {
        OutfitTable.selectAll().map(Outfit::fromResultRow)
    }

    override suspend fun getById(id: Int): Outfit? = dbQuery {
        OutfitTable.selectAll().where { OutfitTable.id eq id }
            .map(Outfit::fromResultRow).singleOrNull()
    }
}