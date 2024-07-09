package dev.ise.shoppingmap.dto

import kotlinx.serialization.Serializable

@Serializable
data class Capsule(
    val id: Int = -1,
    val name: String = "",
    val description: String = "",
    val image_id: Int = -1,
    val outfits: List<Int>
)
