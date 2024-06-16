package dev.ise.dto

import kotlinx.serialization.Serializable

@Serializable
data class Outfit(
    val id: Int = -1,
    val name: String = "",
    val description: String = "",
    val image_id: Int = -1,
    val clothes: MutableList<Cloth> = mutableListOf(),
)