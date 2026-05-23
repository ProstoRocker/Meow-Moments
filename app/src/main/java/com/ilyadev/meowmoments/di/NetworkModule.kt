package com.ilyadev.meowmoments.di

import android.content.Context
import com.ilyadev.meowmoments.data.remote.api.CatFactsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        // Создаем кэш на 10 МБ
        val cache = Cache(context.cacheDir.resolve("http_cache"), 10 * 1024 * 1024)

        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            // Добавляем кастомный interceptor для управления кэшированием
            .addInterceptor { chain ->
                var request = chain.request()

                // Добавляем заголовок для кэширования (если сеть недоступна, используем кэш)
                request = request.newBuilder()
                    .header("Cache-Control", "public, max-age=3600") // Кэшируем на 1 час
                    .build()

                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideCatFactsApiService(okHttpClient: OkHttpClient): CatFactsApiService {
        return Retrofit.Builder()
            .baseUrl("https://catfact.ninja/") // Базовый URL для Cat Facts API
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatFactsApiService::class.java)
    }
}