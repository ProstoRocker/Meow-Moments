package com.ilyadev.meowmoments.domain.usecase

import com.ilyadev.meowmoments.domain.model.CatFact
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import javax.inject.Inject

/**
 * UseCase для получения факта дня.
 */
interface GetTodayFactUseCase {
    /**
     * Возвращает факт, предназначенный для отображения в текущий день.
     * @return [CatFact] или null, если факты закончились или произошла ошибка.
     */
    suspend operator fun invoke(): CatFact?
}

// Реализация
class GetTodayFactUseCaseImpl @Inject constructor(
    private val repository: CatFactsRepository
) : GetTodayFactUseCase {
    override suspend fun invoke(): CatFact? {
        return repository.getFactForToday() // Предполагается, что репозиторий сам знает, как получить факт дня
    }
}