package dev.ise.shoppingmap.mics

import io.github.cdimascio.dotenv.dotenv
import kotlin.properties.ReadOnlyProperty

val POSTGRES_USER by environment("shoppingMap")
val POSTGRES_PASSWORD by environment("shoppingMap")
val POSTGRES_DB by environment("shoppingMap")
val POSTGRES_HOST by environment("localhost")
val POSTGRES_PORT by environment(5432)
val IMAGE_MODULE_URL by environment("http://localhost:8000")
val CORES by environment(Runtime.getRuntime().availableProcessors())

inline fun <reified T : Any> environment(defaultValue: T): ReadOnlyProperty<Any?, T> = ReadOnlyProperty { _, property ->
    val envValue = System.getProperty(property.name) ?: dotenv { ignoreIfMissing = true }[property.name]
    ?: System.getenv(property.name)

    when (T::class) {
        String::class -> envValue ?: defaultValue
        Int::class -> envValue?.toIntOrNull() ?: defaultValue
        Long::class -> envValue?.toLongOrNull() ?: defaultValue
        Boolean::class -> envValue?.toBooleanStrictOrNull() ?: defaultValue
        Double::class -> envValue?.toDoubleOrNull() ?: defaultValue
        Float::class -> envValue?.toFloatOrNull() ?: defaultValue
        else -> defaultValue
    } as T
}