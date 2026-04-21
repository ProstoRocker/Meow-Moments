package com.ilyadev.meowmoments.domain.repository

import com.ilyadev.meowmoments.domain.model.CatFact

interface CatFactsRepository {
    /**
     * Получает факт для текущего дня.
     * Если сегодняшний день новый, выбирает и сохраняет новый факт.
     */
    suspend fun getFactForToday(): CatFact?


}