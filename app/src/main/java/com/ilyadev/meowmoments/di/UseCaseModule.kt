package com.ilyadev.meowmoments.di

import com.ilyadev.meowmoments.domain.usecase.GetTodayFactUseCase
import com.ilyadev.meowmoments.domain.usecase.GetTodayFactUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    @Singleton
    abstract fun bindGetTodayFactUseCase(
        getTodayFactUseCaseImpl: GetTodayFactUseCaseImpl
    ): GetTodayFactUseCase
}