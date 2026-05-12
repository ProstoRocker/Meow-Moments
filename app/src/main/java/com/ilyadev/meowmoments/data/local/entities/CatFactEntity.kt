package com.ilyadev.meowmoments.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cat_facts")
data class CatFactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val category: String = "API", // Все факты из API будут категории "API"
    val imageUrl: String? = null, // URL из Cataas будет здесь
    val isFavorite: Boolean = false
)