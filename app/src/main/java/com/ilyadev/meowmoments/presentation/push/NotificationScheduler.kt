package com.ilyadev.meowmoments.presentation.push

import android.content.Context
import androidx.work.*
import com.ilyadev.meowmoments.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.TimeUnit

@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository
) {

    companion object {
        private const val WORK_NAME = "daily_fact_notification"
    }

    fun scheduleDailyFactNotification() {
        // Получаем настройки уведомлений
        val shouldNotify = settingsRepository.getDailyNotificationEnabled()
        val notificationTime = settingsRepository.getNotificationTime()

        if (!shouldNotify) {
            cancelDailyFactNotification()
            return
        }

        // Парсим время уведомления (например, "09:00")
        val (hours, minutes) = notificationTime.split(":").map { it.toInt() }

        // Рассчитываем задержку до следующего уведомления
        val now = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = now
            set(java.util.Calendar.HOUR_OF_DAY, hours)
            set(java.util.Calendar.MINUTE, minutes)
            set(java.util.Calendar.SECOND, 0)
        }

        // Если время уже прошло, планируем на завтра
        if (calendar.timeInMillis <= now) {
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }

        val initialDelay = calendar.timeInMillis - now

        // Создаем периодическую работу
        val request = PeriodicWorkRequestBuilder<DailyFactWorker>(
            24, TimeUnit.HOURS,
            initialDelay, TimeUnit.MILLISECONDS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        // Запланировать работу
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                request
            )
    }

    fun cancelDailyFactNotification() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}