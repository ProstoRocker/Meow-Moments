package com.ilyadev.meowmoments.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ilyadev.meowmoments.data.local.entities.CollectedFactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectedFactDao {

    @Query("SELECT * FROM collected_facts WHERE dateCollected = :date")
    fun getFactsForDate(date: String): Flow<List<CollectedFactEntity>>

    @Query("SELECT * FROM collected_facts")
    fun getAllCollectedFacts(): Flow<List<CollectedFactEntity>>

    @Query("SELECT COUNT(*) FROM collected_facts")
    suspend fun getCollectedCount(): Int

    // Новый метод для получения потока количества
    @Query("SELECT COUNT(*) FROM collected_facts")
    fun getCollectedCountAsFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(collectedFact: CollectedFactEntity)

    @Query("SELECT * FROM collected_facts WHERE factId = :factId LIMIT 1")
    suspend fun getCollectedFactByFactId(factId: Long): CollectedFactEntity?
}