package com.ilyadev.meowmoments.presentation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilyadev.meowmoments.domain.model.CatFact
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: CatFactsRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query

        // Отменяем предыдущий поиск
        searchJob?.cancel()

        // Запускаем новый поиск с задержкой (debounce)
        searchJob = viewModelScope.launch {
            delay(300) // 300ms задержка
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            _uiState.value = SearchUiState.Idle
            return
        }

        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            try {
                repository.searchFacts(query).collect { results ->
                    _uiState.value = if (results.isEmpty()) {
                        SearchUiState.NoResults
                    } else {
                        SearchUiState.Results(results)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = SearchUiState.Error("Ошибка поиска: ${e.message}")
            }
        }
    }
}

sealed interface SearchUiState {
    object Idle : SearchUiState // Когда нет запроса
    object Loading : SearchUiState
    object NoResults : SearchUiState
    data class Results(val facts: List<CatFact>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}