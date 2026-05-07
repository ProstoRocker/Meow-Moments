package com.ilyadev.meowmoments.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilyadev.meowmoments.data.local.DatabaseInitializer
import com.ilyadev.meowmoments.domain.model.CatFact
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import com.ilyadev.meowmoments.domain.usecase.GetTodayFactUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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
    private val getTodayFactUseCase: GetTodayFactUseCase,
    private val repository: CatFactsRepository // Используется для получения количества и случайного факта
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
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
    // Теперь вызывает метод из репозитория для получения случайного факта
    fun refreshFact() {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            try {
                // ВАЖНО: вызываем метод репозитория напрямую для получения случайного факта
                val randomFact = repository.getRandomFact()
                _uiState.value = if (randomFact != null) {
                    MainUiState.Success(randomFact)
                } else {
                    MainUiState.Error("Нет доступных фактов для отображения.")
                }
            } catch (e: Exception) {
                _uiState.value = MainUiState.Error(
                    e.message ?: "Неизвестная ошибка при загрузке случайного факта"
                )
            }
        }
    }

    // Новая функция для инициализации базы данных
    fun initializeDatabase(databaseInitializer: DatabaseInitializer) {
        viewModelScope.launch {
            databaseInitializer.initializeDatabase()
            loadTodayFact() // После инициализации загружаем факт
        }
    }

    // Правильная реализация получения количества собранных фактов
    // Возвращаем Flow, который можно наблюдать
    fun getCollectedCount(): Flow<Int> {
        return repository.getCollectedCountAsFlow()
        // distinctUntilChanged() можно добавить, если нужно
    }

    // Общее количество фактов (можно заменить на константу или получить из репозитория)
    // Лучше получить из репозитория, если оно там доступно
    fun getTotalFactsCount(): Int {
        // Пока возвращаем фиксированное значение, но можно сделать вызов репозитория
        // return repository.getTotalFactsCount() // если такой метод будет
        return 50 // или другое значение, полученное из репозитория
    }
}