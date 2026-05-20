package com.ilyadev.meowmoments.utils

import android.content.Context

class FeatureAvailabilityManager(private val context: Context) {

    private val trialManager = TrialManager(context)

    fun isPaidFeatureAvailable(feature: PaidFeature): Boolean {
        // Проверяем, является ли приложение платной версией
        val isPaidVersion = context.packageName.endsWith(".paid")
        if (isPaidVersion) {
            return true // В платной версии все функции доступны
        }

        // В бесплатной версии проверяем пробный период
        return when (feature) {
            PaidFeature.CALENDAR -> !trialManager.isTrialExpired()
            PaidFeature.MY_FACTS -> !trialManager.isTrialExpired()
        }
    }

    enum class PaidFeature {
        CALENDAR,
        MY_FACTS
    }
}