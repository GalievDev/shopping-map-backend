package dev.ise.dao

import dev.ise.dto.Capsule

interface CapsuleDAO {
    fun create(name: String, description: String, outfits: List<Int>, imageId: Int): Int
    fun delete(id: Int): Int
    fun getAll(): List<Capsule>
    fun getById(id: Int): Capsule?
}