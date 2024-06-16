package dev.ise.dao

import dev.ise.dto.Outfit

interface OutfitDAO {
    fun create(name: String, description: String, image: ByteArray) : Int
    fun deleteById(id: Int) : Int
    fun getAll() : List<Outfit>
    fun getById(id: Int) : Outfit?
}