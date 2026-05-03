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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(collectedFact: CollectedFactEntity)
}