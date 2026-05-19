package com.ilyadev.meowmoments.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ilyadev.meowmoments.data.local.dao.CatFactDao
import com.ilyadev.meowmoments.data.local.dao.CollectedFactDao
import com.ilyadev.meowmoments.data.local.dao.RecentlyViewedFactDao
import com.ilyadev.meowmoments.data.local.entities.CatFactEntity
import com.ilyadev.meowmoments.data.local.entities.CollectedFactEntity
import com.ilyadev.meowmoments.data.local.entities.RecentlyViewedFactEntity

@Database(
    entities = [
        CatFactEntity::class,
        CollectedFactEntity::class,
        RecentlyViewedFactEntity::class
    ],
    version = 5, // Увеличено до 5 из-за изменения схемы
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catFactDao(): CatFactDao
    abstract fun collectedFactDao(): CollectedFactDao
    abstract fun recentlyViewedFactDao(): RecentlyViewedFactDao
}