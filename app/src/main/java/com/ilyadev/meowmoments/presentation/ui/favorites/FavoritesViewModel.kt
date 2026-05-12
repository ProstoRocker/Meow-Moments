package com.ilyadev.meowmoments.presentation.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilyadev.meowmoments.domain.model.CatFact
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
                repository.getFavoriteFacts().collectLatest { facts ->
                    _uiState.value = if (facts.isEmpty()) {
                        FavoritesUiState.Empty
                    } else {
                        FavoritesUiState.Success(facts)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = FavoritesUiState.Error("Ошибка загрузки избранного: ${e.message}")
            }
        }
    }

    fun toggleFavorite(factId: Long, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateFavoriteStatus(factId, !isCurrentlyFavorite)
                // Состояние обновится автоматически через collectLatest в loadFavoriteFacts
            } catch (e: Exception) {
                // Логирование ошибки
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