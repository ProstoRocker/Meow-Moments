package com.ilyadev.meowmoments.di

import android.content.Context
import androidx.room.Room
import com.ilyadev.meowmoments.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "meow_moments_db"
        )
            .fallbackToDestructiveMigration() // ВНИМАНИЕ: Это удалит данные при изменении схемы! Для продакшена используй Migration.
            .build()
    }

    @Provides
    @Singleton
    fun provideCatFactDao(appDatabase: AppDatabase) = appDatabase.catFactDao()

    @Provides
    @Singleton
    fun provideCollectedFactDao(appDatabase: AppDatabase) = appDatabase.collectedFactDao()

    // --- НОВОЕ: Привязка для RecentlyViewedFactDao ---
    @Provides
    @Singleton
    fun provideRecentlyViewedFactDao(appDatabase: AppDatabase) = appDatabase.recentlyViewedFactDao()
}