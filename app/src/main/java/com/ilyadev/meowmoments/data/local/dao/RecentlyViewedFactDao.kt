package com.ilyadev.meowmoments.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ilyadev.meowmoments.data.local.entities.RecentlyViewedFactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentlyViewedFactDao {

    // Вставляем факт в историю просмотров (заменяем, если уже есть)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViewedFact(recentlyViewedFact: RecentlyViewedFactEntity)

    // Получаем последние N фактов, отсортированные по времени просмотра (новые первыми)
    @Query("SELECT factId FROM recently_viewed_facts ORDER BY viewedAt DESC LIMIT :limit")
    fun getRecentlyViewedFactIds(limit: Int): Flow<List<Long>>

    // Опционально: очистить историю
    @Query("DELETE FROM recently_viewed_facts")
    suspend fun clearHistory()
}