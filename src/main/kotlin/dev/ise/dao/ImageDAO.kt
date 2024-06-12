package dev.ise.dao

import dev.ise.dto.Image
import java.sql.Statement

interface ImageDAO {
    fun create(name: String, byteArray: ByteArray): Statement?
    fun delete(id: Int): Int
    fun getById(id: Int): Image?
}