package com.ilyadev.meowmoments.domain.usecase

import com.ilyadev.meowmoments.domain.model.CatFact

interface GetTodayFactUseCase {
    suspend operator fun invoke(): CatFact?
}