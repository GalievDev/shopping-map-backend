package dev.ise.dao

import dev.ise.dto.Cloth

interface ClothDAO {
    fun create(name: String, link: String, image: ByteArray): Int
    fun deleteById(id: Int): Int
    fun getAll(): List<Cloth>
    fun getById(id: Int): Cloth?
}