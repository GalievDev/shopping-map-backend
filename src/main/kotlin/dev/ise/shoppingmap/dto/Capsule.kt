package dev.ise.shoppingmap.dto

import dev.ise.shoppingmap.table.CapsuleTable
import dev.ise.shoppingmap.table.ClothTable
import dev.ise.shoppingmap.table.relation.CapsulesOutfits
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll

@Serializable
data class Capsule(
    val id: Int = -1,
    val name: String = "",
    val description: String = "",
    val imageId: Int = -1,
    val outfits: List<Int>
) {
    companion object {
        fun fromResultRow(row: ResultRow): Capsule {
            val outfits = CapsulesOutfits.selectAll().where { CapsulesOutfits.capsuleId eq row[CapsuleTable.id] }
                .map { it[ClothTable.id].value }.toList()
            return Capsule(
                id = row[CapsuleTable.id].value,
                name = row[CapsuleTable.name],
                description = row[CapsuleTable.description],
                imageId = row[CapsuleTable.imageId],
                outfits = outfits
            )
        }
    }
}
