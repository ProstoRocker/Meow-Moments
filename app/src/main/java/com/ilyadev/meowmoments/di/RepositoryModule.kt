package com.ilyadev.meowmoments.di

import com.ilyadev.meowmoments.data.repository.CatFactsRepositoryImpl
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCatFactsRepository(
        catFactsRepositoryImpl: CatFactsRepositoryImpl
    ): CatFactsRepository
}