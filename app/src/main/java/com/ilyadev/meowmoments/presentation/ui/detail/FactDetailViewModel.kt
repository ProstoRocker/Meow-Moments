package com.ilyadev.meowmoments.presentation.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilyadev.meowmoments.domain.model.CatFact
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FactDetailViewModel @Inject constructor(
    private val repository: CatFactsRepository // <-- Добавляем репозиторий
) : ViewModel() {

    private val _uiState = MutableStateFlow<FactDetailUiState>(FactDetailUiState.Loading)
    val uiState: StateFlow<FactDetailUiState> = _uiState.asStateFlow()

    fun setFact(fact: CatFact) {
        _uiState.value = FactDetailUiState.Success(fact)
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is FactDetailUiState.Success) {
                val fact = currentState.fact
                val newIsFavorite = !fact.isFavorite // Инвертируем статус
                try {
                    repository.updateFavoriteStatus(fact.id, newIsFavorite)
                    // Обновляем состояние UI
                    _uiState.value = FactDetailUiState.Success(
                        fact.copy(isFavorite = newIsFavorite)
                    )
                } catch (e: Exception) {
                    // Логирование ошибки
                    e.printStackTrace()
                }
            }
        }
    }
}