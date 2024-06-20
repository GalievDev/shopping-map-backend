package dev.ise.dao

import dev.ise.dto.Outfit

interface OutfitDAO {
    fun create(name: String, description: String, clothes: List<Int>, imageId: Int) : Int
    fun delete(id: Int) : Int
    fun getAll() : List<Outfit>
    fun getById(id: Int) : Outfit?
}