package dev.ise.shoppingmap.table

import org.jetbrains.exposed.dao.id.IntIdTable

object ImageTable: IntIdTable("images") {
    val name = varchar("name", 255)
    val bytes = text("bytes")
}