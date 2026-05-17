package com.ilyadev.meowmoments.data.repository

import android.content.SharedPreferences
import com.ilyadev.meowmoments.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Реализация SettingsRepository, использующая SharedPreferences для хранения настроек.
 */
class SettingsRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    // --- Константы для SharedPreferences ---
    companion object {
        private const val PREF_USE_SYSTEM_THEME = "pref_use_system_theme"
        private const val PREF_DARK_THEME_ENABLED = "pref_dark_theme_enabled"
        private const val PREF_DAILY_NOTIFICATION_ENABLED = "pref_daily_notification_enabled"
        private const val PREF_NOTIFICATION_TIME = "pref_notification_time"
        private const val PREF_ANIMATIONS_ENABLED = "pref_animations_enabled"

        // Значения по умолчанию
        private const val DEFAULT_USE_SYSTEM_THEME = true
        private const val DEFAULT_DARK_THEME_ENABLED = false
        private const val DEFAULT_DAILY_NOTIFICATION_ENABLED = true
        private const val DEFAULT_NOTIFICATION_TIME = "09:00"
        private const val DEFAULT_ANIMATIONS_ENABLED = true
    }

    // --- Тема приложения ---
    override fun getUseSystemTheme(): Flow<Boolean> = flow {
        val useSystemTheme = sharedPreferences.getBoolean(
            PREF_USE_SYSTEM_THEME, DEFAULT_USE_SYSTEM_THEME
        )
        emit(useSystemTheme)
    }

    override suspend fun setUseSystemTheme(useSystemTheme: Boolean) {
        sharedPreferences.edit()
            .putBoolean(PREF_USE_SYSTEM_THEME, useSystemTheme)
            .apply()
    }

    override fun getDarkThemeEnabled(): Flow<Boolean> = flow {
        val darkThemeEnabled = sharedPreferences.getBoolean(
            PREF_DARK_THEME_ENABLED, DEFAULT_DARK_THEME_ENABLED
        )
        emit(darkThemeEnabled)
    }

    override suspend fun setDarkThemeEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(PREF_DARK_THEME_ENABLED, enabled)
            .apply()
    }

    // --- Уведомления ---
    override fun getDailyNotificationEnabled(): Flow<Boolean> = flow {
        val enabled = sharedPreferences.getBoolean(
            PREF_DAILY_NOTIFICATION_ENABLED, DEFAULT_DAILY_NOTIFICATION_ENABLED
        )
        emit(enabled)
    }

    override suspend fun setDailyNotificationEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(PREF_DAILY_NOTIFICATION_ENABLED, enabled)
            .apply()
    }

    override fun getNotificationTime(): Flow<String> = flow {
        val time = sharedPreferences.getString(
            PREF_NOTIFICATION_TIME, DEFAULT_NOTIFICATION_TIME
        ) ?: DEFAULT_NOTIFICATION_TIME
        emit(time)
    }

    override suspend fun setNotificationTime(time: String) {
        sharedPreferences.edit()
            .putString(PREF_NOTIFICATION_TIME, time)
            .apply()
    }

    // --- Другие настройки ---
    override fun getAnimationsEnabled(): Flow<Boolean> = flow {
        val enabled = sharedPreferences.getBoolean(
            PREF_ANIMATIONS_ENABLED, DEFAULT_ANIMATIONS_ENABLED
        )
        emit(enabled)
    }

    override suspend fun setAnimationsEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(PREF_ANIMATIONS_ENABLED, enabled)
            .apply()
    }
}