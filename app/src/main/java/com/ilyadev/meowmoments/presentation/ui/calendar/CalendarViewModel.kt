package com.ilyadev.meowmoments.presentation.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: CatFactsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CalendarUiState>(CalendarUiState.Loading)
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private var currentMonth: YearMonth = YearMonth.now()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        loadCalendarData()
    }

    private fun loadCalendarData() {
        viewModelScope.launch {
            _uiState.value = CalendarUiState.Loading

            try {
                // Получаем список собранных дат
                val collectedDates = repository.getAllCollectedFacts()
                    .first()
                    .map { it.dateReceived }
                    .distinct()

                _uiState.value = CalendarUiState.Success(
                    currentMonth = currentMonth,
                    collectedDates = collectedDates,
                    today = LocalDate.now().format(dateFormatter)
                )
            } catch (e: Exception) {
                _uiState.value = CalendarUiState.Error("Ошибка загрузки календаря: ${e.message}")
            }
        }
    }

    fun navigateToPreviousMonth() {
        currentMonth = currentMonth.minusMonths(1)
        updateCalendarState()
    }

    fun navigateToNextMonth() {
        currentMonth = currentMonth.plusMonths(1)
        updateCalendarState()
    }

    private fun updateCalendarState() {
        viewModelScope.launch {
            _uiState.value = CalendarUiState.Loading

            try {
                val collectedDates = repository.getAllCollectedFacts()
                    .first()
                    .map { it.dateReceived }
                    .distinct()

                _uiState.value = CalendarUiState.Success(
                    currentMonth = currentMonth,
                    collectedDates = collectedDates,
                    today = LocalDate.now().format(dateFormatter)
                )
            } catch (e: Exception) {
                _uiState.value = CalendarUiState.Error("Ошибка загрузки календаря: ${e.message}")
            }
        }
    }
}

sealed interface CalendarUiState {
    object Loading : CalendarUiState
    data class Success(
        val currentMonth: YearMonth,
        val collectedDates: List<String>,
        val today: String
    ) : CalendarUiState

    data class Error(val message: String) : CalendarUiState
}