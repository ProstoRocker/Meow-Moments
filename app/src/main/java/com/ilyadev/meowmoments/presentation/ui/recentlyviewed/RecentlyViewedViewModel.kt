package com.ilyadev.meowmoments.presentation.ui.recentlyviewed

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
class RecentlyViewedViewModel @Inject constructor(
    private val repository: CatFactsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecentlyViewedUiState>(RecentlyViewedUiState.Loading)
    val uiState: StateFlow<RecentlyViewedUiState> = _uiState.asStateFlow()

    init {
        loadRecentlyViewedFacts()
    }

    private fun loadRecentlyViewedFacts() {
        viewModelScope.launch {
            _uiState.value = RecentlyViewedUiState.Loading
            try {
                repository.getRecentlyViewedFacts().collectLatest { facts ->
                    _uiState.value = if (facts.isEmpty()) {
                        RecentlyViewedUiState.Empty
                    } else {
                        RecentlyViewedUiState.Success(facts)
                    }
                }
            } catch (e: Exception) {
                _uiState.value =
                    RecentlyViewedUiState.Error("Ошибка загрузки последних: ${e.message}")
            }
        }
    }
}

sealed interface RecentlyViewedUiState {
    object Loading : RecentlyViewedUiState
    object Empty : RecentlyViewedUiState
    data class Success(val facts: List<CatFact>) : RecentlyViewedUiState
    data class Error(val message: String) : RecentlyViewedUiState
}