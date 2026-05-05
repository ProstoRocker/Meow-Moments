package com.ilyadev.meowmoments.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ilyadev.meowmoments.data.local.dao.CatFactDao
import com.ilyadev.meowmoments.data.local.dao.CollectedFactDao
import com.ilyadev.meowmoments.data.local.entities.CatFactEntity
import com.ilyadev.meowmoments.data.local.entities.CollectedFactEntity

@Database(
    entities = [CatFactEntity::class, CollectedFactEntity::class, CatImageEntity::class], // <-- Добавлен CatImageEntity
    version = 2, // <-- Увеличиваем версию базы, так как добавили таблицу
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catFactDao(): CatFactDao
    abstract fun collectedFactDao(): CollectedFactDao
    abstract fun catImageDao(): CatImageDao // <-- Добавлен метод для получения CatImageDao
}