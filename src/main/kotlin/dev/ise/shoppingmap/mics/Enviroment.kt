package dev.ise.shoppingmap.mics

import io.github.cdimascio.dotenv.dotenv
import kotlin.properties.ReadOnlyProperty

val POSTGRES_USER by environment("postgres")
val POSTGRES_PASS by environment("")
val POSTGRES_DB by environment("shopping-map")
val POSTGRES_HOST by environment("localhost")
val POSTGRES_PORT by environment(5432)

inline fun <reified T : Any> environment(defaultValue: T): ReadOnlyProperty<Any?, T> = ReadOnlyProperty { _, property ->
    dotenv { ignoreIfMissing = true }[property.name] as? T ?: System.getenv(property.name) as? T ?: defaultValue
}