package dev.ise.shoppingmap.dto

import dev.ise.shoppingmap.table.ClothTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

@Serializable
data class Cloth(
    val id: Int = -1,
    val name: String = "",
    val link: String = "",
    val description: String = "",
    val type: ClothType = ClothType.NONE,
    val imageId: Int = -1,
) {
    companion object {
        fun fromResultRow(row: ResultRow): Cloth = Cloth(
            id = row[ClothTable.id].value,
            name = row[ClothTable.name],
            link = row[ClothTable.link],
            description = row[ClothTable.description],
            type = ClothType.valueOf(row[ClothTable.type]),
            imageId = row[ClothTable.imageId]
        )
    }
}