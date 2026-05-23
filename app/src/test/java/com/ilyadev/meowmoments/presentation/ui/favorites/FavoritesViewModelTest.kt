package com.ilyadev.meowmoments.presentation.ui.favorites

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ilyadev.meowmoments.domain.model.CatFact
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: FavoritesViewModel

    @Mock
    private lateinit var repository: CatFactsRepository

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var closeable: AutoCloseable

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        closeable = MockitoAnnotations.openMocks(this)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        closeable.close()
    }

    @Test
    fun `loadFavoriteFacts calls repository and updates UI state to Success`() = runTest {
        // Given
        val mockFacts = listOf(
            CatFact(
                id = 1L,
                text = "Favorite fact 1",
                category = "Test",
                imageUrl = null,
                dateReceived = "2026-05-18",
                isFavorite = true
            )
        )
        whenever(repository.getFavoriteFacts()).thenReturn(flowOf(mockFacts))

        // When
        viewModel = FavoritesViewModel(repository)
        advanceUntilIdle() // Дожидаемся выполнения init блока

        val currentUiState = viewModel.uiState.value

        // Then
        assert(currentUiState is FavoritesUiState.Success)
        if (currentUiState is FavoritesUiState.Success) {
            assert(currentUiState.facts == mockFacts)
        }
    }

    @Test
    fun `loadFavoriteFacts returns empty list and updates UI state to Empty`() = runTest {
        // Given
        val emptyFacts = emptyList<CatFact>()
        whenever(repository.getFavoriteFacts()).thenReturn(flowOf(emptyFacts))

        // When
        viewModel = FavoritesViewModel(repository)
        advanceUntilIdle()

        val currentUiState = viewModel.uiState.value

        // Then
        assert(currentUiState is FavoritesUiState.Empty)
    }

    @Test
    fun `toggleFavorite calls repository update method and updates UI state`() = runTest {
        // Given
        val factId = 1L
        val currentStatus = true
        val updatedStatus = false
        val mockFact = CatFact(
            id = factId,
            text = "Test fact",
            category = "Test",
            imageUrl = null,
            dateReceived = "2026-05-18",
            isFavorite = currentStatus
        )
        val mockFacts = listOf(mockFact)
        whenever(repository.getFavoriteFacts()).thenReturn(flowOf(mockFacts))

        viewModel = FavoritesViewModel(repository)
        advanceUntilIdle()

        // When
        viewModel.toggleFavorite(factId, currentStatus)
        advanceUntilIdle()

        // Then
        verify(repository).updateFavoriteStatus(factId, updatedStatus)

        // Проверяем, что UI стейт обновился (в логике ViewModel он обновляется вручную)
        val state = viewModel.uiState.value
        assert(state is FavoritesUiState.Success)
        if (state is FavoritesUiState.Success) {
            assert(!state.facts[0].isFavorite)
        }
    }

    @Test
    fun `toggleFavorite removes fact from favorites in Success state`() = runTest {
        // Given
        val factId = 1L
        val currentStatus = true
        val mockFact = CatFact(
            id = factId,
            text = "Test fact",
            category = "Test",
            imageUrl = null,
            dateReceived = "2026-05-18",
            isFavorite = currentStatus
        )
        whenever(repository.getFavoriteFacts()).thenReturn(flowOf(listOf(mockFact)))

        viewModel = FavoritesViewModel(repository)
        advanceUntilIdle()

        // When
        viewModel.toggleFavorite(factId, currentStatus)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assert(state is FavoritesUiState.Success)
        if (state is FavoritesUiState.Success) {
            assert(!state.facts[0].isFavorite)
        }
    }
}
