package com.ilyadev.meowmoments.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий для управления настройками приложения.
 * Отвечает за сохранение и получение пользовательских предпочтений.
 */
interface SettingsRepository {

    // --- Тема приложения ---
    /**
     * Возвращает Flow с текущим состоянием использования системной темы.
     */
    fun getUseSystemTheme(): Flow<Boolean>

    /**
     * Устанавливает, следует ли использовать системную тему.
     */
    suspend fun setUseSystemTheme(useSystemTheme: Boolean)

    /**
     * Возвращает Flow с текущим состоянием темного режима.
     */
    fun getDarkThemeEnabled(): Flow<Boolean>

    /**
     * Устанавливает состояние темного режима.
     */
    suspend fun setDarkThemeEnabled(enabled: Boolean)

    // --- Уведомления ---
    /**
     * Возвращает Flow с состоянием ежедневных уведомлений.
     */
    fun getDailyNotificationEnabled(): Flow<Boolean>

    /**
     * Включает или отключает ежедневные уведомления.
     */
    suspend fun setDailyNotificationEnabled(enabled: Boolean)

    /**
     * Возвращает Flow с временем отправки уведомлений (в формате "HH:mm").
     */
    fun getNotificationTime(): Flow<String>

    /**
     * Устанавливает время отправки уведомлений (в формате "HH:mm").
     */
    suspend fun setNotificationTime(time: String)

    // --- Другие настройки (можно расширять) ---
    /**
     * Возвращает Flow с состоянием анимации.
     */
    fun getAnimationsEnabled(): Flow<Boolean>

    /**
     * Включает или отключает анимации в приложении.
     */
    suspend fun setAnimationsEnabled(enabled: Boolean)
}