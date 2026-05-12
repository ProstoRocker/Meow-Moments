package com.ilyadev.meowmoments.domain.usecase

import com.ilyadev.meowmoments.domain.model.CatFact
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import javax.inject.Inject

class GetTodayFactUseCaseImpl @Inject constructor(
    private val repository: CatFactsRepository
) : GetTodayFactUseCase {
    override suspend operator fun invoke(): CatFact? {
        return repository.getFactForToday()
    }
}