package com.ilyadev.meowmoments.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilyadev.meowmoments.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // --- Тема ---
    private val _useSystemTheme = MutableStateFlow(true)
    val useSystemTheme: StateFlow<Boolean> = _useSystemTheme.asStateFlow()

    private val _darkThemeEnabled = MutableStateFlow(false)
    val darkThemeEnabled: StateFlow<Boolean> = _darkThemeEnabled.asStateFlow()

    // --- Уведомления ---
    private val _dailyNotificationEnabled = MutableStateFlow(true)
    val dailyNotificationEnabled: StateFlow<Boolean> = _dailyNotificationEnabled.asStateFlow()

    private val _notificationTime = MutableStateFlow("09:00")
    val notificationTime: StateFlow<String> = _notificationTime.asStateFlow()

    // --- Другие настройки ---
    private val _animationsEnabled = MutableStateFlow(true)
    val animationsEnabled: StateFlow<Boolean> = _animationsEnabled.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.getUseSystemTheme().collect { useSystemTheme ->
                _useSystemTheme.value = useSystemTheme
            }
        }

        viewModelScope.launch {
            settingsRepository.getDarkThemeEnabled().collect { darkThemeEnabled ->
                _darkThemeEnabled.value = darkThemeEnabled
            }
        }

        viewModelScope.launch {
            settingsRepository.getDailyNotificationEnabled().collect { enabled ->
                _dailyNotificationEnabled.value = enabled
            }
        }

        viewModelScope.launch {
            settingsRepository.getNotificationTime().collect { time ->
                _notificationTime.value = time
            }
        }

        viewModelScope.launch {
            settingsRepository.getAnimationsEnabled().collect { enabled ->
                _animationsEnabled.value = enabled
            }
        }
    }

    // --- Методы для изменения настроек ---
    fun setUseSystemTheme(useSystemTheme: Boolean) {
        viewModelScope.launch {
            settingsRepository.setUseSystemTheme(useSystemTheme)
        }
    }

    fun setDarkThemeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkThemeEnabled(enabled)
        }
    }

    fun setDailyNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDailyNotificationEnabled(enabled)
        }
    }

    fun setNotificationTime(time: String) {
        viewModelScope.launch {
            settingsRepository.setNotificationTime(time)
        }
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAnimationsEnabled(enabled)
        }
    }
}