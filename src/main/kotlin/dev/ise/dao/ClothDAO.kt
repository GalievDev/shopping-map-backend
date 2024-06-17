package dev.ise.dao

import dev.ise.dto.Cloth
import dev.ise.dto.ClothType

interface ClothDAO {
    fun create(name: String, link: String, description: String, clothType: ClothType, image: ByteArray): Int
    fun deleteById(id: Int): Int
    fun getAll(): List<Cloth>
    fun getById(id: Int): Cloth?
}