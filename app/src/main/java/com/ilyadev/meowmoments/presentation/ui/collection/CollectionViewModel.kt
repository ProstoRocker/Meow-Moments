package com.ilyadev.meowmoments.presentation.ui.collection

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
                val facts = repository.getAllCollectedFacts().collectLatest { facts ->
                    if (facts.isEmpty()) {
                        _uiState.value = CollectionUiState.Empty
                    } else {
                        _uiState.value = CollectionUiState.Success(facts)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = CollectionUiState.Error("Ошибка загрузки коллекции: ${e.message}")
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