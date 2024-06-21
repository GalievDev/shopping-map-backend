package dev.ise.shoppingmap.dao

import dev.ise.shoppingmap.dto.Cloth
import dev.ise.shoppingmap.dto.ClothType

interface ClothDAO {
    fun create(name: String, link: String, description: String, clothType: ClothType, image_id: Int): Int
    fun delete(id: Int): Int
    fun getAll(): List<Cloth>
    fun getById(id: Int): Cloth?
}