package dev.ise.dto

import kotlinx.serialization.Serializable

@Serializable
data class Cloth(
    val id: Int = -1,
    val name: String = "",
    val link: String = "",
    val image: ByteArray = ByteArray(0)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Cloth

        if (id != other.id) return false
        if (name != other.name) return false
        if (link != other.link) return false
        if (!image.contentEquals(other.image)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + link.hashCode()
        result = 31 * result + image.contentHashCode()
        return result
    }
}