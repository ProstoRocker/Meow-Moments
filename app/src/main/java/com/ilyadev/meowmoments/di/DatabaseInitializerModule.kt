package com.ilyadev.meowmoments.di

import com.ilyadev.meowmoments.data.local.DatabaseInitializer
import com.ilyadev.meowmoments.data.local.dao.CatFactDao
import com.ilyadev.meowmoments.data.remote.api.CatFactsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseInitializerModule {

    @Provides
    @Singleton
    fun provideDatabaseInitializer(
        catFactDao: CatFactDao,
        catFactsApiService: CatFactsApiService
    ): DatabaseInitializer {
        return DatabaseInitializer(catFactDao, catFactsApiService)
    }
}