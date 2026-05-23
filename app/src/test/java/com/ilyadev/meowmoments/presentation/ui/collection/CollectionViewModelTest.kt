package com.ilyadev.meowmoments.presentation.ui.collection

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
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
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@kotlinx.coroutines.ExperimentalCoroutinesApi
class CollectionViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CollectionViewModel

    @Mock
    private lateinit var repository: CatFactsRepository

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var closeable: AutoCloseable

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        closeable = MockitoAnnotations.openMocks(this)
        // Mock getPagedCollectedFacts because it is used during initialization
        whenever(repository.getPagedCollectedFacts()).thenReturn(flowOf())
        whenever(repository.getAllCollectedFacts()).thenReturn(flowOf(emptyList()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        closeable.close()
    }

    @Test
    fun `toggleFavorite calls repository update method`() = runTest {
        // Given
        val factId = 1L
        val currentStatus = false
        viewModel = CollectionViewModel(repository)
        advanceUntilIdle()

        // When
        viewModel.toggleFavorite(factId, currentStatus)
        advanceUntilIdle()

        // Then
        verify(repository).updateFavoriteStatus(factId, !currentStatus)
    }

    @Test
    fun `loadCollectedFacts emits Success state with facts`() = runTest {
        // Given
        val mockFacts = emptyList<com.ilyadev.meowmoments.domain.model.CatFact>()
        whenever(repository.getAllCollectedFacts()).thenReturn(flowOf(mockFacts))

        // When
        viewModel = CollectionViewModel(repository)
        advanceUntilIdle()

        // Then
        assert(viewModel.uiState.value is CollectionUiState.Success)
    }
}