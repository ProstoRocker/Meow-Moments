package com.ilyadev.meowmoments.di

import android.content.Context
import com.ilyadev.meowmoments.data.local.DatabaseInitializer
import com.ilyadev.meowmoments.data.local.dao.CatFactDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseInitializerModule {

    @Provides
    @Singleton
    fun provideDatabaseInitializer(
        @ApplicationContext context: Context,
        catFactDao: CatFactDao
    ): DatabaseInitializer {
        return DatabaseInitializer(context, catFactDao)
    }
}