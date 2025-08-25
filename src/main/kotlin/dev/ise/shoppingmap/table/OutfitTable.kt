package dev.ise.shoppingmap.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object OutfitTable: IntIdTable("outfits") {
    val name = varchar("name", 255)
    val description = varchar("description", 255)
    val imageId = integer("imageId").references(ImageTable.id, ReferenceOption.SET_DEFAULT, ReferenceOption.CASCADE).default(1)
}