package dev.ise.shoppingmap.repository

import dev.ise.shoppingmap.dto.Outfit

interface OutfitRepository {
    suspend fun create(outfit: Outfit) : Int
    suspend fun delete(id: Int) : Int
    suspend fun deleteCloth(outfitId: Int, clothId: Int): Int
    suspend fun changeImage(outfitId: Int, newImageId: Int) : Int
    suspend fun getAll() : List<Outfit>
    suspend fun getById(id: Int) : Outfit?
}