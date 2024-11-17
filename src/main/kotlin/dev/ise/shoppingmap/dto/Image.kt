package dev.ise.shoppingmap.dto

import dev.ise.shoppingmap.table.ImageTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

@Serializable
data class Image(
    val name: String = "",
    val bytes: String = ""
) {
    companion object {
        fun fromResultRow(row: ResultRow): Image = Image(
            name = row[ImageTable.name],
            bytes = row[ImageTable.bytes]
        )
    }
}