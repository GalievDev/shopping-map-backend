package dev.ise.shoppingmap.repository.postgre

import dev.ise.shoppingmap.dto.Cloth
import dev.ise.shoppingmap.mics.FAILURE
import dev.ise.shoppingmap.mics.SUCCESS
import dev.ise.shoppingmap.mics.dbQuery
import dev.ise.shoppingmap.repository.ClothRepository
import dev.ise.shoppingmap.table.ClothTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

object PostgresClothRepository: ClothRepository {
    override suspend fun create(cloth: Cloth): Int =
        dbQuery {
            val insertResult = ClothTable.insert {
                it[name] = cloth.name
                it[link] = cloth.link
                it[description] = cloth.description
                it[type] = cloth.type.name
                it[imageId] = cloth.imageId
            }
            if (insertResult.insertedCount > 0) SUCCESS else FAILURE
    }

    override suspend fun delete(id: Int): Int = dbQuery {
        val deleteResult = ClothTable.deleteWhere { ClothTable.id eq id }
        if (deleteResult > 0) SUCCESS else FAILURE
    }

    override suspend fun getAll(): List<Cloth> = dbQuery {
        ClothTable.selectAll().map(Cloth::fromResultRow)
    }

    override suspend fun getById(id: Int): Cloth? = dbQuery {
        ClothTable.selectAll().where { ClothTable.id eq id }
            .map(Cloth::fromResultRow).singleOrNull()
    }
}