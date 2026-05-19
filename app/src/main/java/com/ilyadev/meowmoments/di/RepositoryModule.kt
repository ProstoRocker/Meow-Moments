package com.ilyadev.meowmoments.di

import android.content.Context
import android.content.SharedPreferences
import com.ilyadev.meowmoments.data.repository.CatFactsRepositoryImpl
import com.ilyadev.meowmoments.data.repository.SettingsRepositoryImpl
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import com.ilyadev.meowmoments.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCatFactsRepository(
        impl: CatFactsRepositoryImpl
    ): CatFactsRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository

    companion object {
        @Provides
        @Singleton
        fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
            return context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        }
    }
}
