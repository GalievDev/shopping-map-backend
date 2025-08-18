package dev.ise.shoppingmap.repository.postgre

import dev.ise.shoppingmap.dto.Capsule
import dev.ise.shoppingmap.mics.FAILURE
import dev.ise.shoppingmap.mics.SUCCESS
import dev.ise.shoppingmap.mics.dbQuery
import dev.ise.shoppingmap.repository.CapsuleRepository
import dev.ise.shoppingmap.table.CapsuleTable
import dev.ise.shoppingmap.table.relation.CapsulesOutfits
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object PostgresCapsuleRepository: CapsuleRepository {
    override suspend fun create(capsule: Capsule): Int = dbQuery {
        val insertCapsule = CapsuleTable.insert {
            it[name] = capsule.name
            it[description] = capsule.description
            it[imageId] = capsule.imageId
        }
        capsule.outfits.forEach { id ->
            CapsulesOutfits.insert {
                it[capsuleId] = insertCapsule[CapsuleTable.id].value
                it[outfitId] = id
            }
        }
        if (insertCapsule.insertedCount > 0) SUCCESS else FAILURE
    }

    override suspend fun delete(id: Int): Int = dbQuery {
        val deleteResult = CapsuleTable.deleteWhere { CapsuleTable.id eq id }
        if (deleteResult > 0) SUCCESS else FAILURE
    }

    override suspend fun deleteOutfit(capsuleId: Int, outfitId: Int): Int = dbQuery {
        val deleteResult = CapsulesOutfits
            .deleteWhere { CapsulesOutfits.capsuleId eq capsuleId and(CapsulesOutfits.outfitId eq outfitId) }

        if (deleteResult > 0) SUCCESS else FAILURE
    }

    override suspend fun changeImage(capsuleId: Int, newImageId: Int): Int = dbQuery {
        val updateResult = CapsuleTable.update(where = { CapsuleTable.id eq capsuleId }) {
            it[imageId] = newImageId
        }
        if (updateResult > 0) SUCCESS else FAILURE
    }

    override suspend fun getAll(): List<Capsule> = dbQuery {
        CapsuleTable.selectAll().map(Capsule::fromResultRow)
    }

    override suspend fun getById(id: Int): Capsule? = dbQuery {
        CapsuleTable.selectAll().where { CapsuleTable.id eq id }
            .map(Capsule::fromResultRow).singleOrNull()
    }
}