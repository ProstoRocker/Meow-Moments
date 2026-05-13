package com.ilyadev.meowmoments.presentation.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilyadev.meowmoments.domain.model.CatFact
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: CatFactsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavoriteFacts()
    }

    private fun loadFavoriteFacts() {
        viewModelScope.launch {
            _uiState.value = FavoritesUiState.Loading
            try {
                // Просто запускаем Flow, но не используем collectLatest для обновления UI
                repository.getFavoriteFacts().collect { facts ->
                    _uiState.value = if (facts.isEmpty()) {
                        FavoritesUiState.Empty
                    } else {
                        FavoritesUiState.Success(facts)
                    }
                    // Опять же, для вручную управляемого списка, collectLatest не нужен
                }
            } catch (e: Exception) {
                _uiState.value = FavoritesUiState.Error("Ошибка загрузки избранного: ${e.message}")
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
                        is FavoritesUiState.Success -> {
                            val updatedList = currentState.facts.map { fact ->
                                if (fact.id == factId) {
                                    fact.copy(isFavorite = !isCurrentlyFavorite)
                                } else {
                                    fact
                                }
                            }
                            // Опционально: если после удаления из избранного список стал пустым,
                            // можно переключиться на Empty состояние.
                            if (updatedList.isEmpty()) {
                                FavoritesUiState.Empty
                            } else {
                                FavoritesUiState.Success(updatedList)
                            }
                        }
                        // Если состояние не Success, ничего не меняем
                        is FavoritesUiState.Loading,
                        is FavoritesUiState.Empty,
                        is FavoritesUiState.Error -> currentState
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

sealed interface FavoritesUiState {
    object Loading : FavoritesUiState
    object Empty : FavoritesUiState
    data class Success(val facts: List<CatFact>) : FavoritesUiState
    data class Error(val message: String) : FavoritesUiState
}