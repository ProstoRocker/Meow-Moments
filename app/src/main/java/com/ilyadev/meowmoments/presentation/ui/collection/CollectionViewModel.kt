package com.ilyadev.meowmoments.presentation.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilyadev.meowmoments.domain.model.CatFact
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update // Импортируем update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val repository: CatFactsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CollectionUiState>(CollectionUiState.Loading)
    val uiState: StateFlow<CollectionUiState> = _uiState.asStateFlow()

    init {
        loadCollectedFacts()
    }

    private fun loadCollectedFacts() {
        viewModelScope.launch {
            _uiState.value = CollectionUiState.Loading
            try {
                // Просто запускаем Flow, но не используем collectLatest для обновления UI
                repository.getAllCollectedFacts().collect { facts ->
                    // Обновляем UI, только если это начальная загрузка или ошибка
                    if (_uiState.value !is CollectionUiState.Success) {
                        _uiState.value = CollectionUiState.Success(facts)
                    }
                    // Или просто обновляем, если список изменился глобально (например, добавился новый факт дня)
                    // Для нашего случая, мы будем вручную управлять списком после начальной загрузки
                }
            } catch (e: Exception) {
                _uiState.value = CollectionUiState.Error("Ошибка загрузки коллекции: ${e.message}")
            }
        }
    }

    // --- ОБНОВЛЁННЫЙ МЕТОД ---
    fun toggleFavorite(factId: Long, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            try {
                // 1. Обновить статус в базе данных
                repository.updateFavoriteStatus(factId, !isCurrentlyFavorite)

                // 2. Найти текущий список и обновить конкретный элемент
                _uiState.update { currentState ->
                    when (currentState) {
                        is CollectionUiState.Success -> {
                            val updatedList = currentState.facts.map { fact ->
                                if (fact.id == factId) {
                                    fact.copy(isFavorite = !isCurrentlyFavorite)
                                } else {
                                    fact
                                }
                            }
                            CollectionUiState.Success(updatedList)
                        }
                        // Если состояние не Success, ничего не меняем
                        is CollectionUiState.Loading,
                        is CollectionUiState.Empty,
                        is CollectionUiState.Error -> currentState
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Можно обновить UI, чтобы показать ошибку, если нужно
            }
        }
    }
}

sealed interface CollectionUiState {
    object Loading : CollectionUiState
    object Empty : CollectionUiState
    data class Success(val facts: List<CatFact>) : CollectionUiState
    data class Error(val message: String) : CollectionUiState
}