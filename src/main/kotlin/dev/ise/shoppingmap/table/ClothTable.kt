package dev.ise.shoppingmap.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object ClothTable: IntIdTable("clothes") {
    val name = varchar("name", 255)
    val link = varchar("link", 255)
    val description = varchar("description", 255)
    val type = varchar("type", 50)
    val imageId = integer("imageId").references(ImageTable.id, ReferenceOption.SET_DEFAULT, ReferenceOption.CASCADE).default(1)
}