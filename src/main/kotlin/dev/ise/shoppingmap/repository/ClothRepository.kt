package dev.ise.shoppingmap.repository

import dev.ise.shoppingmap.dto.Cloth

interface ClothRepository {
    suspend fun create(cloth: Cloth): Int
    suspend fun delete(id: Int): Int
    suspend fun getAll(): List<Cloth>
    suspend fun getById(id: Int): Cloth?
}