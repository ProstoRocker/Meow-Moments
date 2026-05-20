package com.ilyadev.meowmoments.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.apps.common.testing.accessibility.framework.BuildConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ilyadev.meowmoments.R
import com.ilyadev.meowmoments.data.local.DatabaseInitializer
import com.ilyadev.meowmoments.domain.repository.SettingsRepository
import com.ilyadev.meowmoments.presentation.ui.main.MainViewModel
import com.ilyadev.meowmoments.utils.FeatureAvailabilityManager
import com.ilyadev.meowmoments.utils.FeatureAvailabilityManager.PaidFeature
import com.ilyadev.meowmoments.utils.TrialManager
import com.ilyadev.meowmoments.work.SyncFactsWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var databaseInitializer: DatabaseInitializer

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val viewModel: MainViewModel by viewModels()

    private lateinit var featureAvailabilityManager: FeatureAvailabilityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализируем TrialManager и FeatureAvailabilityManager
        val trialManager = TrialManager(this)
        featureAvailabilityManager = FeatureAvailabilityManager(this)

        // Запускаем пробный период при первом запуске (только для бесплатной версии)
        if (BuildConfig.FLAVOR == "free") {
            trialManager.startTrial()
        }

        // Применяем тему до вызова setContentView
        applyTheme()

        setContentView(R.layout.activity_main)

        // Инициализируем базу данных
        initializeDatabase()

        // Запланировать фоновую синхронизацию
        scheduleBackgroundSync()

        // Настройка навигации
        setupNavigation(savedInstanceState)
    }

    /**
     * Применяет тему приложения в зависимости от настроек пользователя.
     */
    private fun applyTheme() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsRepository.getUseSystemTheme().collect { useSystemTheme ->
                    val mode = if (useSystemTheme) {
                        androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    } else {
                        // Загружаем состояние темного режима
                        val darkThemeEnabled = settingsRepository.getDarkThemeEnabled().first()
                        if (darkThemeEnabled) androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES else androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
                    }
                    androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(mode)
                }
            }
        }
    }

    private fun initializeDatabase() {
        viewModel.initializeDatabase(databaseInitializer)
    }

    private fun setupNavigation(savedInstanceState: Bundle?) {
        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        // Для бесплатной версии: настраиваем проверку доступности функций
        if (BuildConfig.FLAVOR == "free") {
            navView.setOnItemSelectedListener { item ->
                val isAvailable = when (item.itemId) {
                    R.id.calendarFragment -> featureAvailabilityManager.isPaidFeatureAvailable(PaidFeature.CALENDAR)
                    R.id.myFactsFragment -> featureAvailabilityManager.isPaidFeatureAvailable(PaidFeature.MY_FACTS)
                    else -> true
                }

                if (isAvailable) {
                    NavigationUI.onNavDestinationSelected(item, navController)
                    true
                } else {
                    showTrialExpiredDialog()
                    false
                }
            }
        } else {
            // Для платной версии: стандартная навигация
            navView.setupWithNavController(navController)
        }

        // Установка начального фрагмента (если не установлен)
        if (savedInstanceState == null) {
            navController.navigate(R.id.mainFragment)
        }
    }

    private fun scheduleBackgroundSync() {
        val syncWork = PeriodicWorkRequestBuilder<SyncFactsWorker>(
            repeatInterval = 1, // Повторять
            repeatIntervalTimeUnit = TimeUnit.DAYS // раз в день
        )
            .setInitialDelay(1, TimeUnit.HOURS) // Первый запуск через 1 час
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "sync_facts_work", // Уникальное имя
                ExistingPeriodicWorkPolicy.KEEP, // Не перезаписывать
                syncWork
            )
    }

    private fun showTrialExpiredDialog() {
        AlertDialog.Builder(this)
            .setTitle("Пробный период истек")
            .setMessage("Доступ к этой функции ограничен. Приобретите платную версию приложения.")
            .setPositiveButton("ОК") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}