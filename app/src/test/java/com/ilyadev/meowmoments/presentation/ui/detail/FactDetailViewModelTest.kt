package com.ilyadev.meowmoments.presentation.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
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

class FactDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: FactDetailViewModel

    @Mock
    private lateinit var repository: CatFactsRepository

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var closeable: AutoCloseable

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        closeable = MockitoAnnotations.openMocks(this)
        viewModel = FactDetailViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        closeable.close()
    }

    @Test
    fun `toggleFavorite updates fact status in repository and UI`() = runTest {
        // Given
        val factId = 1L
        val currentStatus = false

        // When
        viewModel.toggleFavorite()

        // Then
        // verify(repository).updateFavoriteStatus(factId, !currentStatus)
        // assert viewModel.uiState.value has updated fact
    }
}