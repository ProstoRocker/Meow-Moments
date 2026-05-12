package com.ilyadev.meowmoments.presentation.ui.collection

import app.cash.turbine.test
import com.ilyadev.meowmoments.domain.model.CatFact
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class CollectionViewModelTest {

    @Mock
    private lateinit var mockRepository: CatFactsRepository

    private lateinit var viewModel: CollectionViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CollectionViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState emits Loading then Empty when no facts collected`() = runTest {
        // Arrange
        whenever(mockRepository.getAllCollectedFacts()).thenReturn(flowOf(emptyList()))

        // Act & Assert
        viewModel.uiState.test {
            assertEquals(CollectionUiState.Loading, awaitItem())
            assertEquals(CollectionUiState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState emits Loading then Success with facts when facts collected`() = runTest {
        // Arrange
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val facts = listOf(
            CatFact(1, "Fact 1", "Category", null, today),
            CatFact(2, "Fact 2", "Category", null, today)
        )
        whenever(mockRepository.getAllCollectedFacts()).thenReturn(flowOf(facts))

        // Act & Assert
        viewModel.uiState.test {
            assertEquals(CollectionUiState.Loading, awaitItem())
            val successState = awaitItem()
            assertTrue(successState is CollectionUiState.Success)
            assertEquals(facts, (successState as CollectionUiState.Success).facts)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState emits Loading then Error when repository throws exception`() = runTest {
        // Arrange
        val errorMessage = "Network error"
        whenever(mockRepository.getAllCollectedFacts()).thenThrow(RuntimeException(errorMessage))

        // Act & Assert
        viewModel.uiState.test {
            assertEquals(CollectionUiState.Loading, awaitItem())
            val errorState = awaitItem()
            assertTrue(errorState is CollectionUiState.Error)
            assertEquals(errorMessage, (errorState as CollectionUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }
}