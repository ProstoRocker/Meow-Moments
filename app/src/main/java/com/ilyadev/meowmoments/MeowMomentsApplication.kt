// =============================================================================
// MeowMomentsApplication.kt
// Главный класс приложения: инициализация Hilt, WorkManager и Coil
// Отвечает за настройку DI, фоновых задач и кэширования изображений
// =============================================================================

package com.ilyadev.meowmoments

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MeowMomentsApplication : Application(), Configuration.Provider, ImageLoaderFactory {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory   // Фабрика Worker'ов с поддержкой Hilt

    // Конфигурация WorkManager с использованием HiltWorkerFactory для DI в Worker'ах
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    // Настройка Coil для загрузки изображений с кэшированием
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25) // 25% от доступной памяти
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024) // 50 MB
                    .build()
            }
            .build()
    }
}