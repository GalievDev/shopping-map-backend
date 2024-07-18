package dev.ise.shoppingmap.dao

import dev.ise.shoppingmap.dto.Outfit

interface OutfitDAO {
    fun create(name: String, description: String, clothes: List<Int>, imageId: Int) : Int
    fun delete(id: Int) : Int
    fun deleteCloth(outfitId: Int, clothId: Int): Int
    fun changeImage(outfitId: Int, imageId: Int) : Int
    fun getAll() : List<Outfit>
    fun getById(id: Int) : Outfit?
}