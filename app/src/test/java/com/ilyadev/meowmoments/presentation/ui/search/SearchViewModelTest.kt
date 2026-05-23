package com.ilyadev.meowmoments.presentation.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ilyadev.meowmoments.domain.model.CatFact
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SearchViewModel

    @Mock
    private lateinit var repository: CatFactsRepository

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var closeable: AutoCloseable

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        closeable = MockitoAnnotations.openMocks(this)
        viewModel = SearchViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        closeable.close()
    }

    @Test
    fun `initial state is SearchUiState Idle`() {
        assertEquals(SearchUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `updateSearchQuery with valid input performs search and returns results`() = runTest {
        // Given
        val query = "cat"
        val facts = listOf(
            CatFact(1, "Cats are cool", "General", null, "2023-10-27")
        )
        `when`(repository.searchFacts(query)).thenReturn(flowOf(facts))

        // When
        viewModel.updateSearchQuery(query)
        advanceTimeBy(301) // Wait for debounce

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.Results)
        assertEquals(facts, (state as SearchUiState.Results).facts)
        verify(repository).searchFacts(query)
    }

    @Test
    fun `updateSearchQuery with query yielding no results shows no results state`() = runTest {
        // Given
        val query = "empty"
        `when`(repository.searchFacts(query)).thenReturn(flowOf(emptyList()))

        // When
        viewModel.updateSearchQuery(query)
        advanceTimeBy(301)

        // Then
        assertEquals(SearchUiState.NoResults, viewModel.uiState.value)
    }

    @Test
    fun `updateSearchQuery with empty input shows idle state immediately`() = runTest {
        // Given
        val query = ""

        // When
        viewModel.updateSearchQuery(query)

        // Then
        assertEquals(SearchUiState.Idle, viewModel.uiState.value)
        verify(repository, never()).searchFacts(anyString())
    }

    @Test
    fun `updateSearchQuery with blank input shows idle state immediately`() = runTest {
        // Given
        val query = "   "

        // When
        viewModel.updateSearchQuery(query)

        // Then
        assertEquals(SearchUiState.Idle, viewModel.uiState.value)
        verify(repository, never()).searchFacts(anyString())
    }

    @Test
    fun `updateSearchQuery trims whitespace and performs search`() = runTest {
        // Given
        val query = "  cat  "
        val trimmedQuery = "cat"
        val facts = listOf(CatFact(1, "Text", "Category", null, "2023"))
        `when`(repository.searchFacts(trimmedQuery)).thenReturn(flowOf(facts))

        // When
        viewModel.updateSearchQuery(query)
        advanceTimeBy(301)

        // Then
        assertTrue(viewModel.uiState.value is SearchUiState.Results)
        verify(repository).searchFacts(trimmedQuery)
    }

    @Test
    fun `updateSearchQuery handles repository error`() = runTest {
        // Given
        val query = "error"
        val errorMessage = "Network error"
        `when`(repository.searchFacts(query)).thenReturn(flow {
            throw Exception(errorMessage)
        })

        // When
        viewModel.updateSearchQuery(query)
        advanceTimeBy(301)

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.Error)
        assertTrue((state as SearchUiState.Error).message.contains(errorMessage))
    }

    @Test
    fun `updateSearchQuery handles repository error after partial results`() = runTest {
        // Given
        val query = "cat"
        val facts = listOf(CatFact(1, "Text", "Category", null, "2023"))
        val errorMessage = "Connection lost"
        `when`(repository.searchFacts(query)).thenReturn(flow {
            emit(facts)
            throw Exception(errorMessage)
        })

        // When
        viewModel.updateSearchQuery(query)
        advanceTimeBy(301)

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.Error)
        assertTrue((state as SearchUiState.Error).message.contains(errorMessage))
    }

    @Test
    fun `updateSearchQuery implements debounce`() = runTest {
        // Given
        val query1 = "c"
        val query2 = "ca"
        val query3 = "cat"
        `when`(repository.searchFacts(query3)).thenReturn(flowOf(emptyList()))

        // When
        viewModel.updateSearchQuery(query1)
        advanceTimeBy(100)
        viewModel.updateSearchQuery(query2)
        advanceTimeBy(100)
        viewModel.updateSearchQuery(query3)
        advanceTimeBy(301)

        // Then
        verify(repository, times(1)).searchFacts(query3)
        verify(repository, never()).searchFacts(query1)
        verify(repository, never()).searchFacts(query2)
    }

    @Test
    fun `updateSearchQuery shows Loading state before results`() = runTest {
        // Given
        val query = "cat"
        val facts = listOf(CatFact(1, "Text", "Category", null, "2023"))
        `when`(repository.searchFacts(query)).thenReturn(flow {
            delay(100) // Simulate delay in flow emission
            emit(facts)
        })

        // When
        viewModel.updateSearchQuery(query)
        advanceTimeBy(301) // Pass debounce

        // Then
        assertEquals(SearchUiState.Loading, viewModel.uiState.value)

        // When
        advanceTimeBy(101) // Allow flow to emit

        // Then
        assertTrue(viewModel.uiState.value is SearchUiState.Results)
        assertEquals(facts, (viewModel.uiState.value as SearchUiState.Results).facts)
    }

    @Test
    fun `UI state updates when repository emits multiple times`() = runTest {
        // Given
        val query = "cat"
        val facts1 = listOf(CatFact(1, "Text 1", "Category", null, "2023"))
        val facts2 = listOf(
            CatFact(1, "Text 1", "Category", null, "2023"),
            CatFact(2, "Text 2", "Category", null, "2023")
        )

        val flow = MutableSharedFlow<List<CatFact>>()
        `when`(repository.searchFacts(query)).thenReturn(flow)

        // When
        viewModel.updateSearchQuery(query)
        advanceTimeBy(301)

        assertEquals(SearchUiState.Loading, viewModel.uiState.value)

        // First emission
        flow.emit(facts1)
        assertEquals(SearchUiState.Results(facts1), viewModel.uiState.value)

        // Second emission
        flow.emit(facts2)
        assertEquals(SearchUiState.Results(facts2), viewModel.uiState.value)
    }

    @Test
    fun `new search cancels previous search collection results`() = runTest {
        // Given
        val query1 = "cat"
        val query2 = "dog"
        val flow1 = MutableSharedFlow<List<CatFact>>()
        val flow2 = MutableSharedFlow<List<CatFact>>()

        `when`(repository.searchFacts(query1)).thenReturn(flow1)
        `when`(repository.searchFacts(query2)).thenReturn(flow2)

        // When: Start first search
        viewModel.updateSearchQuery(query1)
        advanceTimeBy(301)
        assertEquals(SearchUiState.Loading, viewModel.uiState.value)

        // When: Start second search
        viewModel.updateSearchQuery(query2)
        advanceTimeBy(301)

        // When: First search emits
        flow1.emit(listOf(CatFact(1, "Cat", "C", null, "2023")))

        // Then: State should still be Loading or dog results, NOT cat results
        val state = viewModel.uiState.value
        if (state is SearchUiState.Results) {
            assertTrue(state.facts.none { it.text.contains("Cat") })
        }
    }

    @Test
    fun `clearing search query while loading transitions to Idle immediately`() = runTest {
        // Given
        val query = "cat"
        `when`(repository.searchFacts(query)).thenReturn(MutableSharedFlow())

        // Start search
        viewModel.updateSearchQuery(query)
        advanceTimeBy(301)
        assertEquals(SearchUiState.Loading, viewModel.uiState.value)

        // When: Clear query
        viewModel.updateSearchQuery("")

        // Then: Should be Idle immediately, without waiting another 300ms
        assertEquals(SearchUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `updateSearchQuery with same query does not trigger new search`() = runTest {
        // Given
        val query = "cat"
        `when`(repository.searchFacts(query)).thenReturn(flowOf(emptyList()))

        // When
        viewModel.updateSearchQuery(query)
        advanceTimeBy(301)
        verify(repository, times(1)).searchFacts(query)

        // Repeat the same query
        viewModel.updateSearchQuery(query)
        advanceTimeBy(301)

        // Then: verify still only 1 call
        verify(repository, times(1)).searchFacts(query)
    }

    // Helper for anyString()
    private fun anyString(): String = org.mockito.ArgumentMatchers.anyString() ?: ""
}
