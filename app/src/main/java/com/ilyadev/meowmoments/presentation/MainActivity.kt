package com.ilyadev.meowmoments.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ilyadev.meowmoments.R
import com.ilyadev.meowmoments.data.local.DatabaseInitializer
import com.ilyadev.meowmoments.domain.repository.SettingsRepository
import com.ilyadev.meowmoments.presentation.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var databaseInitializer: DatabaseInitializer

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Применяем тему до вызова setContentView
        applyTheme()

        setContentView(R.layout.activity_main)

        // Инициализируем базу данных
        initializeDatabase()

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)
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
}