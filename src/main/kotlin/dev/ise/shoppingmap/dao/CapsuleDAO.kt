package dev.ise.shoppingmap.dao

import dev.ise.shoppingmap.dto.Capsule

interface CapsuleDAO {
    fun create(name: String, description: String, outfits: List<Int>, imageId: Int): Int
    fun delete(id: Int): Int
    fun getAll(): List<Capsule>
    fun getById(id: Int): Capsule?
}