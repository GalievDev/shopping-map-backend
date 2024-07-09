package dev.ise.shoppingmap.dto

import kotlinx.serialization.Serializable

@Serializable
data class Cloth(
    val id: Int = -1,
    val name: String = "",
    val link: String = "",
    val description: String = "",
    val type: ClothType = ClothType.NONE,
    val image_id: Int = -1,
)