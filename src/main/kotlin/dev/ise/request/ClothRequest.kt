package dev.ise.request

import kotlinx.serialization.Serializable

@Serializable
data class ClothRequest(val name: String, val link: String, val image: String) {
}
