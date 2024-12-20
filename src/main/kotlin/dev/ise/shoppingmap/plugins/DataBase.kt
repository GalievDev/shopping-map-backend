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
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDataBase() {
    Database.connect(HikariDataSource(HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = "jdbc:postgresql://$POSTGRES_HOST:$POSTGRES_PORT/$POSTGRES_DB"
        username = POSTGRES_USER
        password = POSTGRES_PASS
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }))

    transaction {
        SchemaUtils.create(ImageTable)
        SchemaUtils.create(ClothTable)
        SchemaUtils.create(OutfitTable)
        SchemaUtils.create(OutfitsClothes)
        SchemaUtils.create(CapsuleTable)
        SchemaUtils.create(CapsulesOutfits)
    }
}