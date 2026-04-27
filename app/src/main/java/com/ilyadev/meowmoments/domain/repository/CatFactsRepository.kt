package com.ilyadev.meowmoments.domain.repository

import com.ilyadev.meowmoments.domain.model.CatFact
import kotlinx.coroutines.flow.Flow

interface CatFactsRepository {
    /**
     * Получает факт для текущего дня.
     * Если сегодняшний день новый, выбирает и сохраняет новый факт.
     */
    suspend fun getFactForToday(): CatFact?
    fun getAllCollectedFacts(): Flow<List<CatFact>>
    suspend fun getCollectedCount(): Int
}