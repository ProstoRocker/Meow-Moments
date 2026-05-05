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
}