package dev.ise.mics

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.intellij.lang.annotations.Language
import java.sql.ResultSet
import java.sql.Statement

object Database {
    private val dataSource: HikariDataSource by lazy {
        HikariDataSource(HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = "jdbc:postgresql://$POSTGRES_HOST:$POSTGRES_PORT/$POSTGRES_DB"
            username = POSTGRES_USER
            password = POSTGRES_PASS
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        })
    }

    fun dataStore(): HikariDataSource = dataSource

    fun query(@Language("PostgreSQL") sql: String, resultSet: (ResultSet) -> Unit) {
        runCatching {
            dataStore().connection.use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeQuery(sql).use { resultSet ->
                        resultSet(resultSet)
                    }
                }
            }
        }.onFailure { println(it.localizedMessage) }
    }

    fun update(@Language("PostgreSQL") sql: String): Statement? {
        runCatching {
            dataStore().connection.use { connection ->
                val statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                statement.executeUpdate(sql)
                return statement
            }
        }.onFailure { println(it.localizedMessage) }
        return null
    }
}