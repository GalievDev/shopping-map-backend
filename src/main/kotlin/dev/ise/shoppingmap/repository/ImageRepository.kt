package dev.ise.shoppingmap.repository

import dev.ise.shoppingmap.dto.Image

interface ImageRepository {
    suspend fun create(image: Image): Int
    suspend fun delete(id: Int): Int
    suspend fun getById(id: Int): Image?
}