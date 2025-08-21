package dev.ise.shoppingmap.repository.postgre

import dev.ise.shoppingmap.dto.Image
import dev.ise.shoppingmap.mics.FAILURE
import dev.ise.shoppingmap.mics.SUCCESS
import dev.ise.shoppingmap.mics.dbQuery
import dev.ise.shoppingmap.repository.ImageRepository
import dev.ise.shoppingmap.table.ImageTable
import io.ktor.util.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

object PostgresImageRepository: ImageRepository {
    override suspend fun create(image: Image): Int = dbQuery {
        val insertResult = ImageTable.insert {
            it[name] = image.name
            it[bytes] = image.bytes.decodeBase64Bytes()
        }
        insertResult[ImageTable.id].value
    }

    override suspend fun delete(id: Int): Int = dbQuery {
        val deleteResult = ImageTable.deleteWhere { ImageTable.id eq id }
        if (deleteResult > 0) SUCCESS else FAILURE
    }

    override suspend fun getById(id: Int): Image? = dbQuery {
        ImageTable.selectAll().where { ImageTable.id eq id }
            .map(Image::fromResultRow).singleOrNull()
    }

}