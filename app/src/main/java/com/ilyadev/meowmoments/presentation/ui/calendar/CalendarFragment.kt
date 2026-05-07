package com.ilyadev.meowmoments.presentation.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.ilyadev.meowmoments.R
import com.ilyadev.meowmoments.databinding.FragmentCalendarBinding
import com.ilyadev.meowmoments.domain.model.CatFact
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@AndroidEntryPoint
class CalendarFragment : Fragment() {

    private val viewModel: CalendarViewModel by viewModels()
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var dayAdapter: CalendarDayAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCalendar()
        setupObservers()
        setupClickListeners()
    }

    private fun setupCalendar() {
        dayAdapter = CalendarDayAdapter { date ->
            showFactsForDate(date)
        }
        binding.rvCalendarDays.apply {
            adapter = dayAdapter
            layoutManager = GridLayoutManager(context, 7)
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is CalendarUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.contentLayout.visibility = View.GONE
                            binding.rvCalendarDays.visibility = View.GONE
                            binding.tvNoFacts.visibility = View.GONE
                        }

                        is CalendarUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.contentLayout.visibility = View.VISIBLE
                            binding.rvCalendarDays.visibility = View.VISIBLE

                            // Обновляем заголовок месяца и года
                            val monthName = state.currentMonth.month.getDisplayName(
                                TextStyle.FULL, Locale.getDefault()
                            )
                            val year = state.currentMonth.year
                            binding.tvMonthYear.text =
                                getString(R.string.month_year_format, monthName, year)

                            // Обновляем календарь
                            updateCalendarDays(
                                state.currentMonth,
                                state.collectedDates,
                                state.today
                            )

                            // Проверяем, есть ли собранные факты за текущий день
                            val today = LocalDate.now()
                            val hasFactsToday = state.collectedDates.contains(
                                today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            )
                            binding.tvNoFacts.isVisible = !hasFactsToday
                        }

                        is CalendarUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.contentLayout.visibility = View.GONE
                            binding.rvCalendarDays.visibility = View.GONE
                            binding.tvNoFacts.visibility = View.GONE
                            // Можно показать сообщение об ошибке
                        }
                    }
                }
            }
        }
    }

    private fun updateCalendarDays(
        currentMonth: YearMonth,
        collectedDates: List<String>,
        todayString: String
    ) {
        val daysInMonth = currentMonth.lengthOfMonth()
        val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value
        val daysList = mutableListOf<CalendarDayItem>()

        // Добавляем дни предыдущего месяца
        val previousMonth = currentMonth.minusMonths(1)
        val daysInPreviousMonth = previousMonth.lengthOfMonth()
        for (i in firstDayOfMonth downTo 1) {
            val date = previousMonth.atDay(daysInPreviousMonth - i + 1)
            daysList.add(CalendarDayItem(date, false, false, false))
        }

        // Добавляем дни текущего месяца
        val today = LocalDate.now()
        for (day in 1..daysInMonth) {
            val date = currentMonth.atDay(day)
            val isToday = date.isEqual(today)
            val isCollected = collectedDates.contains(date.format(DateTimeFormatter.ISO_LOCAL_DATE))
            daysList.add(CalendarDayItem(date, true, isToday, isCollected))
        }

        // Добавляем дни следующего месяца
        val remainingSlots = 42 - daysList.size // 6 недель по 7 дней
        for (day in 1..remainingSlots) {
            val date = currentMonth.plusMonths(1).atDay(day)
            daysList.add(CalendarDayItem(date, false, false, false))
        }

        dayAdapter.submitList(daysList)
    }

    private fun setupClickListeners() {
        binding.btnPreviousMonth.setOnClickListener {
            viewModel.navigateToPreviousMonth()
        }

        binding.btnNextMonth.setOnClickListener {
            viewModel.navigateToNextMonth()
        }
    }

    private fun showFactsForDate(date: LocalDate) {

        val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val factsForDate = viewModel.uiState.value.let { state ->
            if (state is CalendarUiState.Success) {
                state.collectedDates.filter { it == dateStr }
                    .map { CatFact(0, "Пример факта", "Категория", null, it) }
            } else {
                emptyList()
            }
        }

        if (factsForDate.isNotEmpty()) {

            val dialog = FactListDialogFragment.newInstance(factsForDate, dateStr)
            dialog.show(childFragmentManager, "FactListDialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}