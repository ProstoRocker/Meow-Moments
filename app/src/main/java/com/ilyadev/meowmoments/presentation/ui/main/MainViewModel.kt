package com.ilyadev.meowmoments.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilyadev.meowmoments.data.local.DatabaseInitializer
import com.ilyadev.meowmoments.domain.model.CatFact
import com.ilyadev.meowmoments.domain.usecase.GetTodayFactUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Состояние UI для MainFragment
sealed interface MainUiState {
    object Loading : MainUiState
    data class Success(val fact: CatFact) : MainUiState
    data class Error(val message: String) : MainUiState
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getTodayFactUseCase: GetTodayFactUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        // Загружаем факт при инициализации ViewModel
        loadTodayFact()
    }

    private fun loadTodayFact() {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            try {
                val fact = getTodayFactUseCase()
                _uiState.value = if (fact != null) {
                    MainUiState.Success(fact)
                } else {
                    MainUiState.Error("Факты закончились или произошла ошибка.")
                }
            } catch (e: Exception) {
                _uiState.value = MainUiState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    // Функция для обновления факта (например, по кнопке "ещё один")
    fun refreshFact() {
        loadTodayFact() // Просто перезапускаем загрузку
    }

    // Новая функция для инициализации базы данных
    fun initializeDatabase(databaseInitializer: DatabaseInitializer) {
        viewModelScope.launch {
            databaseInitializer.initializeDatabase()
            loadTodayFact() // После инициализации загружаем факт
        }
    }
}