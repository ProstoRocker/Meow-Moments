package com.ilyadev.meowmoments.presentation.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import com.ilyadev.meowmoments.domain.usecase.GetTodayFactUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

// Тесты для ViewModel

class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel

    @Mock
    private lateinit var getTodayFactUseCase: GetTodayFactUseCase

    @Mock
    private lateinit var repository: CatFactsRepository

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var closeable: AutoCloseable

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        closeable = MockitoAnnotations.openMocks(this)
        viewModel = MainViewModel(getTodayFactUseCase, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        closeable.close()
    }

    @Test
    fun `refreshFact updates UI state to Success`() = runTest {
        // Given
        // mock repository.getRandomFact() returns a valid CatFact

        // When
        viewModel.refreshFact()

        // Then
        // assert viewModel.uiState.value is MainUiState.Success
    }

    @Test
    fun `refreshFact updates UI state to Error on failure`() = runTest {
        // Given
        // mock repository.getRandomFact() throws exception

        // When
        viewModel.refreshFact()

        // Then
        // assert viewModel.uiState.value is MainUiState.Error
    }
}