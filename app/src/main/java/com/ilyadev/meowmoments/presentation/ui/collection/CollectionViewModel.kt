package com.ilyadev.meowmoments.presentation.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
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
class CollectionViewModel @Inject constructor(
    private val repository: CatFactsRepository
) : ViewModel() {

    // --- СТАРЫЙ КОД (для совместимости с существующим UI) ---
    private val _uiState = MutableStateFlow<CollectionUiState>(CollectionUiState.Loading)
    val uiState: StateFlow<CollectionUiState> = _uiState.asStateFlow()

    // --- НОВЫЙ КОД (для пагинации) ---
    val pagedFacts = repository.getPagedCollectedFacts()
        .cachedIn(viewModelScope)

    init {
        loadCollectedFacts()
    }

    private fun loadCollectedFacts() {
        viewModelScope.launch {
            _uiState.value = CollectionUiState.Loading
            try {
                repository.getAllCollectedFacts().collect { facts ->
                    if (_uiState.value !is CollectionUiState.Success) {
                        _uiState.value = CollectionUiState.Success(facts)
                    }
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
                repository.updateFavoriteStatus(factId, !isCurrentlyFavorite)

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

                        is CollectionUiState.Loading,
                        is CollectionUiState.Empty,
                        is CollectionUiState.Error -> currentState
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
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