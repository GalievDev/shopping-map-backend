package dev.ise.shoppingmap.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.ise.shoppingmap.mics.*
import dev.ise.shoppingmap.table.CapsuleTable
import dev.ise.shoppingmap.table.ClothTable
import dev.ise.shoppingmap.table.ImageTable
import dev.ise.shoppingmap.table.OutfitTable
import dev.ise.shoppingmap.table.relation.CapsulesOutfits
import dev.ise.shoppingmap.table.relation.OutfitsClothes
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

fun configureDataBase() {
    Database.connect(HikariDataSource(HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = "jdbc:postgresql://$POSTGRES_HOST:$POSTGRES_PORT/$POSTGRES_DB"
        username = POSTGRES_USER
        password = POSTGRES_PASSWORD
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }))

    transaction {
        SchemaUtils.create(
            ImageTable, ClothTable, OutfitTable, OutfitsClothes, CapsuleTable, CapsulesOutfits
        )
        ImageTable.insert {
            it[name] = "default_image"
            it[bytes] = object {}.javaClass.getResourceAsStream("/default/default.jpg")!!.readAllBytes()
        }
    }
}