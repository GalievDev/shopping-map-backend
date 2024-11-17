package dev.ise.shoppingmap.repository

import dev.ise.shoppingmap.dto.Capsule

interface CapsuleRepository {
    suspend fun create(capsule: Capsule): Int
    suspend fun delete(id: Int): Int
    suspend fun deleteOutfit(capsuleId: Int, outfitId: Int): Int
    suspend fun changeImage(capsuleId: Int, newImageId: Int): Int
    suspend fun getAll(): List<Capsule>
    suspend fun getById(id: Int): Capsule?
}