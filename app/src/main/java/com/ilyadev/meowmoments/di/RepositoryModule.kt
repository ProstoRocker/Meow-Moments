package com.ilyadev.meowmoments.di

import android.content.Context
import android.content.SharedPreferences
import com.ilyadev.meowmoments.data.repository.SettingsRepositoryImpl
import com.ilyadev.meowmoments.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Чтобы Dagger Hilt мог предоставить SettingsRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        sharedPreferences: SharedPreferences
    ): SettingsRepository {
        return SettingsRepositoryImpl(sharedPreferences)
    }
}