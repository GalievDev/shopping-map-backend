package dev.ise.shoppingmap.dto

import dev.ise.shoppingmap.table.ClothTable
import dev.ise.shoppingmap.table.OutfitTable
import dev.ise.shoppingmap.table.relation.OutfitsClothes
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll

@Serializable
data class Outfit(
    val id: Int = -1,
    val name: String = "",
    val description: String = "",
    val imageId: Int = -1,
    val clothes: List<Int> = emptyList(),
) {
    companion object {
        fun fromResultRow(row: ResultRow): Outfit {
            val clothes = OutfitsClothes.selectAll().where { OutfitsClothes.outfitId eq row[OutfitTable.id] }
                .map { it[ClothTable.id].value }.toList()
            return Outfit(
                id = row[OutfitTable.id].value,
                name = row[OutfitTable.name],
                description = row[OutfitTable.description],
                imageId = row[OutfitTable.imageId],
                clothes = clothes
            )
        }
    }
}