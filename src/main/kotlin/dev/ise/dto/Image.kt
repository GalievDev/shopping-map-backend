package dev.ise.dto

import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val id: Int = -1,
    val name: String = "",
    val bytes: String = ""
)