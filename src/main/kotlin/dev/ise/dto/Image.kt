package dev.ise.dto

import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val id: Int = -1,
    val name: String = "",
    val byteArray: ByteArray = ByteArray(0)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Image

        if (name != other.name) return false
        if (!byteArray.contentEquals(other.byteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + byteArray.contentHashCode()
        return result
    }
}