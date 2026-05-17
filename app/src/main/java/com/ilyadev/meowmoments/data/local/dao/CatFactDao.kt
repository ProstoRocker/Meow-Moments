package com.ilyadev.meowmoments.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ilyadev.meowmoments.data.local.entities.CatFactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CatFactDao {

    @Query("SELECT * FROM cat_facts")
    fun getAllFacts(): Flow<List<CatFactEntity>>

    @Query("SELECT * FROM cat_facts WHERE id = :id")
    suspend fun getFactById(id: Long): CatFactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(facts: List<CatFactEntity>)

    @Query("SELECT COUNT(*) FROM cat_facts")
    suspend fun getCount(): Int

    // --- СТАРЫЕ МЕТОДЫ ДЛЯ ИЗБРАННОГО ---
    @Query("SELECT * FROM cat_facts WHERE isFavorite = 1 ORDER BY id DESC")
    fun getFavoriteFacts(): Flow<List<CatFactEntity>>

    @Query("UPDATE cat_facts SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)

    // --- МЕТОДЫ ДЛЯ ПОСЛЕДНИХ ПРОСМОТРЕННЫХ ---
    @Query("UPDATE cat_facts SET lastViewedTimestamp = :timestamp WHERE id = :id")
    suspend fun updateLastViewedTimestamp(id: Long, timestamp: Long)

    @Query("SELECT * FROM cat_facts WHERE lastViewedTimestamp IS NOT NULL ORDER BY lastViewedTimestamp DESC")
    fun getRecentlyViewedFacts(): Flow<List<CatFactEntity>>

    @Query("SELECT * FROM cat_facts WHERE lastViewedTimestamp IS NOT NULL ORDER BY lastViewedTimestamp DESC LIMIT :limit")
    suspend fun getRecentlyViewedFactsLimit(limit: Int): List<CatFactEntity>

    @Query("SELECT * FROM cat_facts WHERE id IN (:ids)")
    fun getFactsByIds(ids: List<Long>): Flow<List<CatFactEntity>>

    // --- НОВЫЙ МЕТОД ДЛЯ ПОИСКА ---
    @Query("SELECT * FROM cat_facts WHERE text LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchFactsByText(query: String): Flow<List<CatFactEntity>>
}