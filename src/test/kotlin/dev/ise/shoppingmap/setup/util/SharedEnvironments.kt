package dev.ise.shoppingmap.setup.util

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import dev.ise.shoppingmap.db.Postgres
import org.slf4j.LoggerFactory

object SharedEnvironments {
    init {
        (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).level = Level.INFO
        Postgres
        System.setProperty("CORES", "1")
    }
}