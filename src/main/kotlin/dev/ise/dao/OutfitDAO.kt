package dev.ise.dao

import dev.ise.dto.Outfit

interface OutfitDAO {
    fun create(name: String, description: String, outfitClothIds: List<Int>, image: String) : Int
    fun deleteById(id: Int) : Int
    fun getAll() : List<Outfit>
    fun getById(id: Int) : Outfit?
}