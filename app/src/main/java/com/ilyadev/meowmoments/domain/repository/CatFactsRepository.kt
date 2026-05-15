package com.ilyadev.meowmoments.domain.repository

import com.ilyadev.meowmoments.domain.model.CatFact
import kotlinx.coroutines.flow.Flow

interface CatFactsRepository {
    /**
     * Получает факт для текущего дня.
     * Если сегодняшний день новый, выбирает и сохраняет новый факт.
     */
    suspend fun getFactForToday(): CatFact?

    /**
     * Возвращает Flow со списком всех собранных фактов.
     */
    fun getAllCollectedFacts(): Flow<List<CatFact>>

    /**
     * Возвращает количество собранных фактов (однократно).
     */
    suspend fun getCollectedCount(): Int

    /**
     * Возвращает Flow с количеством собранных фактов.
     */
    fun getCollectedCountAsFlow(): Flow<Int>

    /**
     * Возвращает случайный факт из всех доступных в локальной базе.
     * Может использоваться для кнопки "Ещё один факт".
     */
    suspend fun getRandomFact(): CatFact?

    // --- МЕТОДЫ ДЛЯ ИЗБРАННОГО ---
    fun getFavoriteFacts(): Flow<List<CatFact>>
    suspend fun updateFavoriteStatus(factId: Long, isFavorite: Boolean)

    // --- МЕТОДЫ ДЛЯ ПОСЛЕДНИХ ПРОСМОТРЕННЫХ ---
    suspend fun updateLastViewedTimestamp(factId: Long, timestamp: Long)
    fun getRecentlyViewedFacts(): Flow<List<CatFact>>
}