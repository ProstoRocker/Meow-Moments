package com.ilyadev.meowmoments.utils

import android.content.Context
import android.content.SharedPreferences

class TrialManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("trial_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TRIAL_START_KEY = "trial_start_time"
        private const val TRIAL_DURATION_DAYS = 7L // 7 дней пробного периода
    }

    fun startTrial() {
        if (!prefs.contains(TRIAL_START_KEY)) {
            prefs.edit().putLong(TRIAL_START_KEY, System.currentTimeMillis()).apply()
        }
    }

    fun isTrialExpired(): Boolean {
        val startTime = prefs.getLong(TRIAL_START_KEY, 0L)
        if (startTime == 0L) {
            // Если не был запущен, запускаем сейчас
            startTrial()
            return false
        }
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - startTime
        val trialDurationMillis = TRIAL_DURATION_DAYS * 24 * 60 * 60 * 1000L // 7 дней в миллисекундах
        return elapsedTime > trialDurationMillis
    }

    fun getRemainingDays(): Long {
        val startTime = prefs.getLong(TRIAL_START_KEY, 0L)
        if (startTime == 0L) {
            return TRIAL_DURATION_DAYS // Если не запускался, считаем, что полный период остался
        }
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - startTime
        val trialDurationMillis = TRIAL_DURATION_DAYS * 24 * 60 * 60 * 1000L
        val remainingMillis = trialDurationMillis - elapsedTime
        return if (remainingMillis > 0) remainingMillis / (24 * 60 * 60 * 1000L) else 0L
    }
}