package dev.ise.shoppingmap.table.relation

import dev.ise.shoppingmap.table.ClothTable
import dev.ise.shoppingmap.table.OutfitTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object OutfitsClothes: Table("outfits_clothes") {
    val outfitId = reference(
        "outfit_id", OutfitTable.id,
        ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val clothId = reference(
        "cloth_id", ClothTable.id,
        ReferenceOption.CASCADE, ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(outfitId, clothId)
}