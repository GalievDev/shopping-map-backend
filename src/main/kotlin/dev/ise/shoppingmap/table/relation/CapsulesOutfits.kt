package dev.ise.shoppingmap.table.relation

import dev.ise.shoppingmap.table.CapsuleTable
import dev.ise.shoppingmap.table.OutfitTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object CapsulesOutfits: Table("capsules_outfits") {
    val capsuleId = reference(
        "capsule_id", CapsuleTable.id,
        ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val outfitId = reference(
        "outfit_id", OutfitTable.id,
        ReferenceOption.CASCADE, ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(capsuleId, outfitId)
}