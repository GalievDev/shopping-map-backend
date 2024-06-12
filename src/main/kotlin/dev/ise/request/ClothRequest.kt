package dev.ise.request

data class ClothRequest(val name: String, val link: String, val byteArray: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClothRequest

        if (name != other.name) return false
        if (link != other.link) return false
        if (!byteArray.contentEquals(other.byteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + link.hashCode()
        result = 31 * result + byteArray.contentHashCode()
        return result
    }
}
