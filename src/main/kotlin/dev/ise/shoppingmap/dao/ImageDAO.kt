package dev.ise.shoppingmap.dao

import dev.ise.shoppingmap.dto.Image

interface ImageDAO {
    fun create(name: String, base64: String): Int
    fun delete(id: Int): Int
    fun getById(id: Int): Image?
}