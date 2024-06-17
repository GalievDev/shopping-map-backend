package dev.ise.request

import dev.ise.dto.ClothType
import kotlinx.serialization.Serializable

@Serializable
data class ClothRequest(val name: String, val link: String, val description: String, val image: String, val type: ClothType) {
}
